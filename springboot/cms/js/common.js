

//选择一条记录
function getSelectedRow() {
    var dataInfo = table.checkStatus("tableData");
    var ids = [];
    for (var i = 0; i < dataInfo.data.length; i++) {
        ids.push(dataInfo.data[i].id);
    }
    if (ids.length != 1) {
        layer.alert("请选择一条数据");
        return null;

    }
    return ids[0];
}

//选择多条记录
function getSelectedRows() {
    var dataInfo = table.checkStatus("tableData");
    var ids = [];
    for (var i = 0; i < dataInfo.data.length; i++) {
        ids.push(dataInfo.data[i].id);
    }
    if (ids.length <= 0) {
        layer.alert("至少选择一条数据");
        return null;
    }
    return ids;

}

/*下划线转驼峰*/
function toHumpName(name) {
    return name.replace(/\_(\w)/g, function(all, letter){
        return letter.toUpperCase();
    });
}

/* 驼峰转换下划线*/
function toLineName(name) {
    return name.replace(/([A-Z])/g,"_$1").toLowerCase();
}

function isEmpty(param) {
    if (param == null || $.trim(param) == "") {
        return true;
    }
    return false;
}
