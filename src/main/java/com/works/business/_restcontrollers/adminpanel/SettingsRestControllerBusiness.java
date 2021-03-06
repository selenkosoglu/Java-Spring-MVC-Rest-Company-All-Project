package com.works.business._restcontrollers.adminpanel;

import com.works.entities.security.User;
import com.works.repositories._jpa.UserRepository;
import com.works.utils.REnum;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsRestControllerBusiness {
    final UserRepository userRepository;

    public SettingsRestControllerBusiness(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<REnum, Object> getLocation() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Optional<User> optionalUser = userRepository.findByEmailEquals(SecurityContextHolder.getContext().getAuthentication().getName());
        String[] arr = new String[2];
        if (optionalUser.isPresent()) {//Else olma durumu yok.
            if (!optionalUser.get().getRoles().get(0).getRo_name().equals("ROLE_CUSTOMER")) {//Müşteri değilse
                arr[0] = optionalUser.get().getCompany().getCompany_lat();
                arr[1] = optionalUser.get().getCompany().getCompany_lng();
                hm.put(REnum.RESULT, arr);
                hm.put(REnum.STATUS, true);
                hm.put(REnum.MESSAGE, "İşlem Başarılı");
            } else {
                hm.put(REnum.STATUS, false);
                hm.put(REnum.MESSAGE, "Müşterilerin lokasyon bilgisi kayıt altına alınmamaktadır.");
            }
            return hm;
        }
        return hm;
    }
}
