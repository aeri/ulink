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
                        if(msg.uri == undefined){
                            $("#result").html(
                                    "<div>" +
                                        "<form id='shortenerConfirm' method='post'>" +
                                            "<button type='submit' class='btn btn-primary'>If you click it fails</button>" +
                                        "</form>" +
                                        "<button type='button' class='btn btn-secondary' data-dismiss='modal'>Volver</button>" +
                                    "</div>");
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
    },
    function () {
        $("#shortenerConfirm").submit(
            function (event) {
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