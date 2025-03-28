# Bylith Server Monitoring Project

Spring Boot application for monitoring server health using HTTP(S), FTP, and SSH protocols, built with Maven in Eclipse.

## Features
 - Supports monitoring of servers via HTTP/HTTPS, FTP, and SSH.
 - Stores server data and request history in PostgreSQL.
 - Provides a REST API to manage servers and retrieve their status/history.
 - Sends email alerts for unhealthy servers.
 - Configurable timeout and protocol credentials.

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Eclipse IDE with Maven support
- PostgreSQL 13 or higher
- SMTP server (optional, e.g., Gmail)

## Setup Instructions

### 1. Import the Project into Eclipse
	1. Open Eclipse.
	2. Go to **File → Import**.
	3. Select **Maven → Existing Maven Projects**, click **Next**.
	4. Browse to the project folder (`Bylith_Tsvang`) and click **Finish**.

### 2. Configure PostgreSQL
    Install PostgreSQL and create a database;
    
	Create a database (e.g., defaultdb);
    Update the database connection settings in src/main/resources/application.properties:
   		spring.datasource.url=jdbc:postgresql://localhost:5432/defaultdb
		spring.datasource.username=<your-username>
		spring.datasource.password=<your-password>
		 
### 3. Configure Email Settings
	Update SMTP settings in src/main/resources/application.properties:
	
		spring.mail.host=smtp.gmail.com
		spring.mail.port=587
		spring.mail.username=<your-email>
		spring.mail.password=<your-app-specific-password>
		server.monitor.alert-email=<admin-email>
		
### 4. Configure Protocol Credentials
	Set FTP and SSH credentials in application.properties:	
		ftp.username=<ftp-username>
		ftp.password=<ftp-password>
		ssh.username=<ssh-username>
		ssh.password=<ssh-password>
	Adjust server.monitor.timeout (default: 45000ms) if needed.

### 5. Build the Project
	Right-click the project in Eclipse’s Project Explorer.
	Select Run As > Maven Build....
	In the "Goals" field, enter clean install.
	Click Run to build the project.
	
##	Running the Application
 1. Run in Eclipse:
		Right-click the project in Project Explorer.
		Select Run As > Spring Boot App.
		The application will start on http://localhost:8383 (configurable via server.port).
 2. Verify it’s Running:
		Open a browser or Postman and go to http://localhost:8383/api/servers.
		An empty list [] indicates the server is running.
		
##   Usage
		REST API Endpoints
		Use Postman to interact with the API:
			Add a Server:
			POST http://localhost:8383/api/servers
			Content-Type: application/json
			    {
  				"name": "Test Server",
 				"url": "http://example.com",
 			    "protocol": "HTTP"
			    }
			
			Get Server Details:
				GET http://localhost:8383/api/servers/{id}
			
			Update a Server:
				PUT http://localhost:8383/api/servers/{id}
				Content-Type: application/json
				{
				  "name": "Updated Server",
				  "url": "http://new-example.com",
				  "protocol": "HTTP"
				}
				
			Delete a Server:
				DELETE http://localhost:8383/api/servers/{id}
				
			List All Servers:
				GET http://localhost:8383/api/servers
				
			Get Request History:
				GET http://localhost:8383/api/servers/{id}/history
				
			Check Health at Timestamp:
				GET http://localhost:8383/api/servers/{id}/health-at?timestamp=2025-03-28T12:00:00
				
# Monitoring Logic
	Servers are checked every 60 seconds.
	Status is determined as:
		HEALTHY: Last 5 checks successful.
		UNHEALTHY: 3 consecutive failures.
  		UNKNOWN: Insufficient data or mixed results.
	Email alerts are sent when a server becomes UNHEALTHY.
	
# Configuration
	Edit src/main/resources/application.properties to customize:

		server.port: Application port (default: 8383).
		spring.datasource.*: Database settings.
		spring.mail.*: Email settings.
		server.monitor.timeout: Protocol check timeout (default: 45000ms).
		ftp.* and ssh.*: Protocol credentials.

# Dependencies
	Managed via Maven in pom.xml:

		Spring Boot (Web, JPA, Mail)
		PostgreSQL Driver
		Apache Commons Net (FTP)
		Apache HttpClient 5 (HTTP)
		JSch (SSH)
		Lombok
		
# Database Dump
The SQL dump of the database is included in `dump_sql` in the project root. 
It contains the schema and sample data for the `servers` and `request_history` tables.
				
# Postman Collection
The operational Postman collection is included as `Bylith.postman_collection.json`. 
It simulates all API methods with URLs, parameters, and sample request bodies. Import it into Postman to test the API.

# Environment Variables
This project uses environment variables for sensitive data. Before running, set the following variables:

- `DB_USERNAME`: PostgreSQL username
- `DB_PASSWORD`: PostgreSQL password
- `DB_URL`: PostgreSQL URL
- `MAIL_USERNAME`: SMTP email username
- `MAIL_PASSWORD`: SMTP email password
- `FTP_USERNAME`: FTP client username
- `FTP_PASSWORD`: FTP client password
- `SSH_USERNAME`: SSH client username
- `SSH_PASSWORD`: SSH client password
- `ALERT_EMAIL`: Email address for alerts