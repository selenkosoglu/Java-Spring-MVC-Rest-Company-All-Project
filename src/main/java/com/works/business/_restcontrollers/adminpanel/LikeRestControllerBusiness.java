package com.works.business._restcontrollers.adminpanel;

import com.works.entities.LikeManagement;
import com.works.entities.Product;
import com.works.models._elastic.LikeElasticsearch;
import com.works.models._redis.LikeSession;
import com.works.repositories._elastic.LikeElasticRepository;
import com.works.repositories._jpa.LikeRepository;
import com.works.repositories._jpa.ProductRepository;
import com.works.repositories._jpa.UserRepository;
import com.works.repositories._redis.LikeSessionRepository;
import com.works.utils.REnum;
import com.works.utils.Util;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LikeRestControllerBusiness {
    final LikeRepository likeRepository;
    final LikeElasticRepository likeElasticRepository;
    final LikeSessionRepository likeSessionRepository;
    final ProductRepository productRepository;
    final UserRepository userRepository;

    public LikeRestControllerBusiness(LikeRepository likeRepository, LikeElasticRepository likeElasticRepository, LikeSessionRepository likeSessionRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.likeElasticRepository = likeElasticRepository;
        this.likeSessionRepository = likeSessionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Map<REnum, Object> likeListSearch(String stSearchKey, String stIndex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        hm.put(REnum.STATUS, true);
        hm.put(REnum.RESULT, likeElasticRepository.findByCustomer(stSearchKey, PageRequest.of(Integer.parseInt(stIndex) - 1, Util.pageSize)));
        int additional = 0;
        Integer size = likeElasticRepository.findByCustomer(stSearchKey).size();
        if (size % Util.pageSize != 0) {
            additional = 1;
        }
        hm.put(REnum.ERROR, null);
        hm.put(REnum.COUNTOFPAGE, size / Util.pageSize + additional);
        return hm;
    }

    public Map<REnum, Object> likeList(String stIndex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        hm.put(REnum.STATUS, true);
        hm.put(REnum.RESULT, likeSessionRepository.findByCompanynameEquals(Util.theCompany.getCompany_name(), PageRequest.of(Integer.parseInt(stIndex) - 1, Util.pageSize)));
        int additional = 0;
        if (likeSessionRepository.count() % 10 != 0) {
            additional = 1;
        }
        hm.put(REnum.COUNTOFPAGE, (likeSessionRepository.count() / Util.pageSize) + additional);
        return hm;
    }

    public Map<REnum, Object> allLikeList(String stIndex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        hm.put(REnum.STATUS, true);
        hm.put(REnum.RESULT, likeRepository.findAllLikes());
        return hm;
    }

    public Map<REnum, Object> allLikeList() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        hm.put(REnum.STATUS, true);
        hm.put(REnum.RESULT, likeRepository.findAllLikesAccordingToCustomer());
        return hm;
    }

    public Map<REnum, Object> surveyDelete(String stIndex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.MESSAGE, "Başarılı");
        likeRepository.deleteById(Integer.valueOf(stIndex));
        likeSessionRepository.deleteById(stIndex);
        likeElasticRepository.deleteById(stIndex);
        hm.put(REnum.STATUS, true);
        return hm;
    }

    public Map<REnum, Object> productLike(String stProduct, String stScore) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        LikeManagement likeManagement = new LikeManagement();
        LikeSession likeSession = new LikeSession();
        LikeElasticsearch likeElasticsearch = new LikeElasticsearch();

        //Product IDsi çekme
        Integer productId = 0;
        try {
            productId = Integer.valueOf(stProduct);
        } catch (Exception e) {
            hm.put(REnum.STATUS, false);
            hm.put(REnum.MESSAGE, "Ürün numarası doğal sayı olmalıdır!");
            hm.put(REnum.ERROR, e);
            return hm;
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            likeManagement.setProduct(optionalProduct.get());
            likeSession.setProduct(optionalProduct.get().getPr_name());
            likeElasticsearch.setProduct(optionalProduct.get().getPr_name());
        } else {
            hm.put(REnum.STATUS, false);
            hm.put(REnum.MESSAGE, "Bu numaraya sahip bir ürün veritabanında bulunamadı!");
            return hm;
        }
        Integer score = 0;
        try {
            score = Integer.valueOf(stScore);
        } catch (NumberFormatException e) {
            hm.put(REnum.STATUS, false);
            hm.put(REnum.MESSAGE, "Puanlama doğal sayılar ile yapılmalıdır!");
            hm.put(REnum.ERROR, e);
            return hm;
        }

        //SCORE
        likeManagement.setScore(score);
        likeSession.setScore(stScore);
        likeElasticsearch.setScore(stScore);
        boolean isValid = false;
        //CUSTOMER
        likeManagement.setCustomer(userRepository.findByEmailEquals(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        likeSession.setCustomer(userRepository.findByEmailEquals(SecurityContextHolder.getContext().getAuthentication().getName()).get().getName());
        likeElasticsearch.setCustomer(userRepository.findByEmailEquals(SecurityContextHolder.getContext().getAuthentication().getName()).get().getName());
        for (int i = 0; i < likeManagement.getCustomer().getRoles().size(); i++) {
            if (likeManagement.getCustomer().getRoles().get(i).getRo_name().equals("ROLE_CUSTOMER")) {
                isValid = true;
            }
        }
        //Oy Vermiş mi?
        Optional<LikeManagement> optional = likeRepository.findByCustomer_IdEqualsAndProduct_IdEquals(likeManagement.getCustomer().getId(), productId);
        if (optional.isPresent()) {
            System.out.println("Daha önce oy vermiş.");
            hm.put(REnum.STATUS, false);
            hm.put(REnum.MESSAGE, "Daha önce oy verdiğiniz bir ürüne tekrar oy veremezsiniz.");
            return hm;
        }
        if (isValid) {
            hm.put(REnum.STATUS, true);
            hm.put(REnum.MESSAGE, "Başarılı");
            likeManagement = likeRepository.save(likeManagement); //normal veritabanına save ediyor
            likeSession.setId(String.valueOf(likeManagement.getId())); //sessionin idsini set ediyoruz
            likeElasticsearch.setId(String.valueOf(likeManagement.getId()));
            likeSession.setDate(new Date().toString());
            hm.put(REnum.RESULT, likeSessionRepository.save(likeSession));
            return hm;
        } else {
            hm.put(REnum.STATUS, false);
            hm.put(REnum.MESSAGE, "Oy kullanabilmek için Müşteri olmanız gerekir!");
            return hm;
        }








    }

}
