function getData(connectionSettings, sql_statement) {

    var db = new Database(connectionSettings['Connection URL'], connectionSettings['Username'], connectionSettings['Password']);
    var dbResult = db.query(sql_statement['SQL Statement']);
    db.close();

    //if data is not in the tabular format it needs to be formatted here in other handlers
    return dbResult;
}