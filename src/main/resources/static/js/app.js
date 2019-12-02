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
                            $('#confirmationModal').appendTo("body").modal('show');
                        }
                        else{
                            $("[data-dismiss=modal]").trigger({ type: "click" });
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                + "</a></div>"
                                + "<div class='alert alert-info' role='alert'><font size='3'>Here is the code to access your link stadistics</font>"
                                + "<br><strong><font size='5'>"
                                + msg.code
                                + "</font></strong></div>");
                            
                            $("#qr-code").html(
                                "<div class='text-center'>" +
                                    "<img src='https://chart.googleapis.com/chart?cht=qr&chl=" + msg.uri + "&chs=160x160&chld=L|0'" +
                                        "class='qr-code img-thumbnail img-responsive'> </div>");
                        }
                            
                    },
                    error: function () {
                        $("[data-dismiss=modal]").trigger({ type: "click" });
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
                    $("[data-dismiss=modal]").trigger({ type: "click" });
                    $("#result").html(
                        "<div class='alert alert-success lead'><a target='_blank' href='"
                        + msg.uri
                        + "'>"
                        + msg.uri
                        + "</a></div>"
                        + "<div class='alert alert-info' role='alert'><font size='3'>Here is the code to access your link stadistics</font>"
                        + "<br><strong><font size='5'>"
                        + msg.code
                        + "</font></strong></div>");
                    

                    $("#qr-code").html(
                        "<div class='text-center'>" +
                            "<img src='https://chart.googleapis.com/chart?cht=qr&chl=" + msg.uri + "&chs=160x160&chld=L|0'" +
                                "class='qr-code img-thumbnail img-responsive'> </div>");
                        

                },
                error: function () {
                    $("[data-dismiss=modal]").trigger({ type: "click" });
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



    