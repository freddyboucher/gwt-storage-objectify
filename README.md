gwt-storage-objectify [![Build Status](https://travis-ci.org/freddyboucher/gwt-storage-objectify.svg?branch=master)](https://travis-ci.org/freddyboucher/gwt-storage-objectify)
===========

This Spring Boot / GWT project is a proof of concept that stores in the Browser Local Storage some App Engine Objectify Entities.

Live demo: [https://gwt-storage-objectify.appspot.com/](https://gwt-storage-objectify.appspot.com/)

It uses:
- [GWT (Google Web Toolkit)](http://www.gwtproject.org/)
- REST/JSON service with shared JAX-RS 2.1 interface
  - server: [Spring Boot](https://spring.io/projects/spring-boot) with [Resteasy](http://resteasy.jboss.org/) as JAX-RS Application
  - client [RestyGWT](http://resty-gwt.github.io/)
- [GWT Logging](http://www.gwtproject.org/doc/latest/DevGuideLogging.html)
- [GAE (Google App Engine)](http://developers.google.com/appengine/)
- [Objectify](https://github.com/objectify/objectify)
- [GWT HTML5 Storage](http://www.gwtproject.org/doc/latest/DevGuideHtml5Storage.html)
- [Bean Validation](https://github.com/ManfredTremmel/gwt-bean-validators)
- [Guava (& Guava GWT)](https://github.com/google/guava)

It uses [tbroyer/gwt-maven-plugin](https://github.com/tbroyer/gwt-maven-plugin) and follows [tbroyer/gwt-maven-archetypes](https://github.com/tbroyer/gwt-maven-archetypes) recommended  project structure.

To use it in **prod** mode:
- in */gwt-storage-objectify/* ➜ `mvn clean install` (only one time)
- in */gwt-storage-objectify/gwt-storage-objectify-server/* ➜ `mvn appengine:devserver`
- then open your browser at [http://localhost:8888/](http://localhost:8888/)

To use it in **dev** mode:
- in */gwt-storage-objectify/* ➜ `mvn install -pl gwt-storage-objectify-shared -am` (only one time)
- in */gwt-storage-objectify/gwt-storage-objectify-server/* ➜ `mvn appengine:devserver -Denv=dev -am`
- in */gwt-storage-objectify/gwt-storage-objectify-client/* ➜ `mvn gwt:codeserver -am`
- then open your browser at [http://localhost:8888/](http://localhost:8888/)