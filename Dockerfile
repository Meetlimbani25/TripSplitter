FROM tomcat:10.1-jdk17-temurin

# Remove default tomcat apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory structure
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

# Copy WebContent to ROOT
COPY WebContent /usr/local/tomcat/webapps/ROOT/

# Copy source files
COPY src /tmp/src

# Compile Java files
# We include the Jakarta Servlet API and the MySQL connector in the classpath.
RUN javac -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    -cp "/usr/local/tomcat/lib/*:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*" \
    $(find /tmp/src -name "*.java")

# Expose port 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
