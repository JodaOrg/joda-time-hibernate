
Joda-Time Contributions area - Hibernate support
================================================
Joda-Time is a date and time library that vastly improves on the JDK.
This release provides additional support for Hibernate database persistence.
See http://www.hibernate.org/ for more details on Hibernate.

Additional setup for test cases
-------------------------------
Joda-Time supports the use of maven for the build process.
Maven tries to download all dependencies from Maven Central.
However, at the time of writing, the Hibernate dependencies are not present.

Add the JBoss repository to you setup to load the dependencies:
https://repository.jboss.org/nexus/content/groups/public-jboss/
