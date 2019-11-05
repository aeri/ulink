$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                localStorage.setItem("senturl", $(this).serialize());
                $.ajax({
                    type: "POST",
                    url: "/link",
                    data: $(this).serialize(),
                    success: function (msg) {
                        if(!msg.confirmed){
                            $("#result").html("<div> </div>");
                            $("#qr-code").html("<div> </div>");
                            $("#modal").html(
                                "<div class='modal' id='confirmationModal' tabindex='-1' role='dialog'>"
                                + "<div class='modal-dialog' role='document'>"
                                + "<div class='modal-content'>"
                                + "<div class='modal-header'>"
                                + "<h3 class='modal-title'>Warning</h3>"
                                + "</div>"
                                + "<div class='modal-body'>"
                                + "<p>Submitted URL may not be reachable. Do you still want to shorten it?</p>"
                                + "</div>"
                                + "<div class='modal-footer'>"
                                + "<form action='' class='col-lg-12' id='shortenerConfirm' role='form'>"
                                + "<button type='button' class='btn btn-secondary' data-dismiss='modal'>Cancel</button>"
                                + "<button type='submit' class='btn btn-primary'>Confirm</button>"
                                + "</form>"
                                + "</div>"
                                + "</div>"
                                + "</div>"
                                + "</div>");
                            $("#confirmationModal").modal();
                        }
                        else{
                            $('#confirmationModal').hide();
                            $('body').removeClass('modal-open');
                            $('.modal-backdrop').remove();
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                + "</a></div>");
                            
                            $("#qr-code").html(
                                "<div class='text-center'>" +
                                    "<img src='https://chart.googleapis.com/chart?cht=qr&chl=" + msg.uri + "&chs=160x160&chld=L|0'" +
                                        "class='qr-code img-thumbnail img-responsive'> </div>");
                        }
                            
                    },
                    error: function () {
                        $('#confirmationModal').hide();
                        $('body').removeClass('modal-open');
                        $('.modal-backdrop').remove();
                        $("#modal").html("<div> </div>");
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                        $("#qr-code").html("<div> </div>");
                    }
                });
            });
        $(document).on('submit','#shortenerConfirm', function (event) {
            event.preventDefault();
            $.ajax({
                type: "POST",
                url: "/linkConfirm",
                data: localStorage.getItem("senturl"),
                success: function (msg) {
                    $('#confirmationModal').hide();
                    $('body').removeClass('modal-open');
                    $('.modal-backdrop').remove();
                    $("#modal").html("<div> </div>");
                    $("#result").html(
                        "<div class='alert alert-success lead'><a target='_blank' href='"
                        + msg.uri
                        + "'>"
                        + msg.uri
                        + "</a></div>");
                    

                    $("#qr-code").html(
                        "<div class='text-center'>" +
                            "<img src='https://chart.googleapis.com/chart?cht=qr&chl=" + msg.uri + "&chs=160x160&chld=L|0'" +
                                "class='qr-code img-thumbnail img-responsive'> </div>");
                        

                },
                error: function () {
                    $('#confirmationModal').hide();
                    $('body').removeClass('modal-open');
                    $('.modal-backdrop').remove();
                    $("#modal").html("<div> </div>");
                    $("#result").html(
                        "<div class='alert alert-danger lead'>ERROR</div>");
                    $("#qr-code").html("<div> </div>");
                }
            });
        });
        $("#stadistics").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/stadistics",
                    success: function (msg) { 
                            
                    },
                    error: function () {
                       
                    }
                });
            });
        }
    );



    