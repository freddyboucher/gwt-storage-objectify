gwt-storage-objectify
===========

This GWT project is a proof of concept that stores in the Browser Local Storage some Objectify Entities.

Live demo: [https://gwt-storage-objectify.appspot.com/](https://gwt-storage-objectify.appspot.com/)

It uses:
- [GWT (Google Web Toolkit)](http://www.gwtproject.org/)
- [GWT RPC (Server Communication)](http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html)
- [GWT Logging](http://www.gwtproject.org/doc/latest/DevGuideLogging.html)
- [GAE (Google App Engine)](http://developers.google.com/appengine/)
- [Objectify](https://github.com/objectify/objectify)
- [Objectify GWT](https://github.com/objectify/objectify-gwt)
- [GWT Storage](https://github.com/seanchenxi/gwt-storage)
- [Bean Validation](http://www.gwtproject.org/doc/latest/DevGuideValidation.html)
- [Guava (& Guava GWT)](https://github.com/google/guava)

It uses [tbroyer/gwt-maven-plugin](https://github.com/tbroyer/gwt-maven-plugin) and follows [tbroyer/gwt-maven-archetypes](https://github.com/tbroyer/gwt-maven-archetypes) recommended  project structure.

To use it in **prod** mode:
- in */gwt-storage-objectify/* ➜ `mvn clean install` (only one time)
- in */gwt-storage-objectify/gwt-storage-objectify-server/* ➜ `mvn appengine:devserver`
- then open your browser at [http://localhost:8888/](http://localhost:8888/)

To use it in **dev** mode:
- in */gwt-storage-objectify/* ➜ `mvn clean install` (only one time)
- in */gwt-storage-objectify/* ➜ `mvn gwt:codeserver -pl gwt-storage-objectify-client -am`
- in */gwt-storage-objectify/gwt-storage-objectify-server/* ➜ `mvn appengine:devserver -Denv=dev`
- then open your browser at [http://localhost:8888/](http://localhost:8888/)