/*jshint esversion: 6 */
/*jslint latedef:false*/
/*jshint -W087 */

let url = "http://localhost:8091/rest/admin/content/datatable/list";
let tempUrl = "http://localhost:8091/rest/admin/content/datatable/list";
let searchKey = "";
$(document).on('keyup', 'input', function () {
    searchKey = this.value;
    if (this.value.trim() == "") {
        console.log("redis");
        tempUrl = url;
        $('#exampleTable').DataTable().ajax.url(tempUrl).load();
    } else {
        console.log("Elastic -->" + this.value);
        tempUrl = url + '/' + this.value;
        console.log("tempUrl = " + tempUrl);
        $('#exampleTable').DataTable().ajax.url(tempUrl).load();
    }
});

$(document).ready(function() {
    var tablem = $('#exampleTable').DataTable({
        //dom: 'Bfrtip',
        "retrieve": true,
        "processing": true,
        "serverSide": true,
        "searching": true,
        "paging": true,
        "dom": '<"top"i>rt<"bottom"lp><"clear">', // for hide default global search box
        "ajax": ({
            "url": tempUrl,
            "type": 'Get',
            "datatype": 'json',
            dataSrc: function (d) {
                debugger;
                d.draw = d.DRAW;
                d.recordsTotal = d.COUNT;   //--> Sayfada bulunan toplam kayıt bilgisini veren kısım
                d.recordsFiltered = d.COUNT;   //--> Paggination için gerekli kısım
                return d.RESULT;
            }
        }),
        "columns": [
            { "data": "id", "name": "id", "autoWidth": true },
            { "data": "content_title", "name": "content_title", "autoWidth": true },
            { "data": "content_brief_description", "name": "content_brief_description", "autoWidth": true },
            { "data": "content_detailed_description", "name": "content_detailed_description", "autoWidth": true },
            { "data": "content_status", "name": "content_status", "autoWidth": true },
            {
                "data": "id", "name": "id", "autoWidth": true,
                "render": function (data) {
                    var a = `<div class="ui buttons" style="float: left">
                    <a href="http://localhost:8091/admin/content/`+data+`" class="ui vertical animated primary button">
                    <div class="hidden content">Düzenle</div>
                    <div class="visible content">
                        <i class="edit icon"></i>
                    </div>
                    </a>
                    <div class="ya da"></div>
                    <button onclick="deleteContent(`+data+`)" class="ui vertical animated negative button">
                    <div class="hidden content">Sil</div>
                    <div class="visible content">
                        <i class="trash icon"></i>
                    </div>
                    </button>
                </div> `;
                    return a;
                }
            }
        ],
        language: {
            "zeroRecords": 'Kayıt Bulunamadı.',
            "infoEmpty": 'Eşleşen Kayıt Bulunamadı.',
            "infoFiltered": ''
        }
    });
});

function deleteContent(index) {
    $.ajax({
        url: 'http://localhost:8091/rest/admin/content/delete/' + index,
        type: 'DELETE',
        contentType: "application/json",
        dataType: 'json',
        success: function (data) {
            debugger;
            $('#exampleTable').DataTable().ajax.reload();
        },
        error: function (err) {
            console.log(err);
        }
    });
}
