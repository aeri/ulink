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
                            $("#result").html(
                                        "<form action='' class='col-lg-12' id='shortenerConfirm' role='form'>" +
                                            "<button class='btn btn-primary' type='submit'>If you click it fails</button>" +
                                        "</form>" +
                                        "<button type='button' class='btn btn-secondary'>Volver</button>" +
                                    "<div class='alert alert-warning lead'><a target='_blank' href='"
                                    + msg.target
                                    + "'>"
                                    + msg.target
                                    + "</a></div>"
                                    + "<div><button type='button' class='confirm-button'>Confirm</button></div>");
                            $("#qr-code").html("<div> </div>");
                        }
                        else{
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
                    $("#result").html(
                        "<div class='alert alert-danger lead'>ERROR</div>");
                    $("#qr-code").html("<div> </div>");
                }
            });
        });
        }
    );



    