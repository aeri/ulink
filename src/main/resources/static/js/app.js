$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/link",
                    data: $(this).serialize(),
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
                    }
                });
            });
    });