function validateConnection(connectionSettings) {
    var isConSuccess = true;
    try {
        var db = new Database(connectionSettings['Connection URL'], connectionSettings['Username'], connectionSettings['Password']);
    } catch (e) {
        isConSuccess = false;
    }
    return isConSuccess;
}

function getData(connectionSettings, sql_statement) {
    var dbResult;
    try {
        var db = new Database(connectionSettings['Connection URL'], connectionSettings['Username'], connectionSettings['Password']);
        dbResult = db.query(sql_statement['SQL Statement']);
        return dbResult;
        //if data is not in the tabular format it needs to be formatted here in other handlers

    } catch (e) {
        throw "You have an error in your SQL syntax";

    } finally {
        db.close();
    }


}