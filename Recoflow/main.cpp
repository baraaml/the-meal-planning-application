#include <iostream>
#include <libpq-fe.h>
#include "dotenv.h"
#include "httplib.h"

using namespace std ;

int main() {
    // Load environment variables from .env file
    dotenv::init();

    // Retrieve environment variables
    const char* dbHost = getenv("DB_HOST");
    const char* dbUser = getenv("DB_USER");
    const char* dbName = getenv("DB_NAME");
    const char* dbPassword = getenv("DB_PASSWORD");

    // Construct the connection string
    string connStr = "host=" + string(dbHost) +
                     " user=" + string(dbUser) +
                     " dbname=" + string(dbName) +
                     " password=" + string(dbPassword);

    // Connect to the database
    PGconn* conn = PQconnectdb(connStr.c_str());

    // Check the connection status
    if (PQstatus(conn) != CONNECTION_OK) {
        cerr << "Connection to database failed: " << PQerrorMessage(conn) << endl;
        PQfinish(conn);
        return 1;
    }

    cout << "Connected to the database successfully!" << endl;

    // Close the connection
    PQfinish(conn);

    httplib::Server svr;

    svr.Get("/", [](const httplib::Request&, httplib::Response& res) {
        res.set_content("Hello, World!", "text/plain");
    });

    svr.listen("0.0.0.0", 9876);
  


    return 0;
}