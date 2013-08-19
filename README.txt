
Joda-Time Contributions area - Hibernate support
================================================
Joda-Time is a date and time library that vastly improves on the JDK.
This separate project provides Hibernate database persistence support.

See the home page for more details:
http://www.joda.org/joda-time-hibernate/

The source code is held primarily at GitHub:
https://github.com/JodaOrg/joda-time-hibernate

Additional setup
----------------
Joda-Time supports the use of maven for the build process.
Maven tries to download all dependencies from Maven Central.
However, at the time of writing, the Hibernate dependencies are not present.

Add the JBoss repository to you setup to load the dependencies:
https://repository.jboss.org/nexus/content/groups/public-jboss/

See http://www.hibernate.org/ for more details on Hibernate.
