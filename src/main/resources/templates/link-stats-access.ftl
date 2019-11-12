<!DOCTYPE html>
<html>
<head>
    <title>ulink</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet"
          type="text/css"/>
    <script src="webjars/jquery/2.1.4/jquery.min.js" type="text/javascript"></script>
    <script src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"
            type="text/javascript"></script>
    <script src="js/app.js" type="text/javascript">
    </script>
</head>
<body>
  <div class="col-lg-12 text-center">
      <h1>ulink</h1>
      <p class="lead">Access to your link stadistics</p>
      <br>
      <form action="/linkStats" class="form" method = "post" id="linkStats">
          <div class="input-group input-group-lg col-sm-offset-4 col-sm-4">
            <label for="shortenedUrl"><h3>Shortened URL</h3></label>
            <input type="text" class="form-control mx-sm-3" name="shortenedUrl"  placeholder="ulink.ga/abcd1234">
          </div>
          <div class="input-group input-group-lg col-sm-offset-4 col-sm-4">
            <label for="code"><h3>Access code</h3></label>
            <input type="password" class="form-control mx-sm-3" name="code" placeholder="code123">
          </div>
          <br>
          <em><font size="3" color="red">${failedAccess}</font></em>
          <br>
          <br>
          <button type="submit" class="btn btn-lg btn-primary">See stadistics</button>
      </form>
  </div>
</body>
</html>