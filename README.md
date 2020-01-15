
<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://ulink.herokuapp.com/">
    <img src="https://imgur.com/S1Rj4LM.png" alt="Logo" width="150" height="150">
  </a>

  <h3 align="center">ulink</h3>

  <p align="center">
    The url shortener made for people, not for business
    <br />
    <br />
    <a href="https://ulink.herokuapp.com/">View Demo</a>
    ·
    <a href="https://github.com/aeri/ulink/issues">Report Bug</a>
  </p>
</p>


![Travis](https://api.travis-ci.com/aeri/ulink.svg?branch=master)


<!-- ABOUT THE PROJECT -->
## About

A simple and modern URL shortener deployed with a docker-compose with a distributed architecture.

This shortener is published on the web (Heroku) on the fly with every commit, so you can be sure that the code here is exactly what you use, guaranteeing that the URLs you deposit on the site are yours alone.

<!-- FEATURED -->
### Interesting Features

* [x] Show global system information. 
* [x] Retrieve statistics of your shortened URL (number of clicks, ranking of origins, browsers and most used platforms of the shortened link).
* [x] Get a QR code associated with your shortened URL to be published anywhere and access quickly.
* [x] Reachability check before creating a short URL and warning the user if he wants to continue with the generation in case of not being reachable.
* [x] Check against the Google Safe Browsing service and notify the customer requesting a redirection if the destination is dangerous.

<!-- ARCH -->
## Architecture

This is the system overview of docker compose deployment (not app core deployment in Heroku):

![Architecture](https://imgur.com/UGBzwm7.png)

The following projects have been used to achieve this:

* **PostDock** - *Dmitriy Paunin* - [postdock](https://github.com/paunin/PostDock)
* **dockprom** - *Stefan Prodan* - [dockprom](https://github.com/stefanprodan/dockprom)

## Documentation

The full documentation generated with Javadoc for this project is available at the following link:

[Documentation](https://aeri.github.io/ulink/)

<!-- CONTACT -->
## Creators

* [aeri](https://github.com/aeri)
* [vpec](https://github.com/vpec)
* [javiermixture17](https://github.com/javiermixture17)



<!-- LICENSE -->
## License

Distributed under GNU General Public License v3.0. See `LICENSE` for more information.