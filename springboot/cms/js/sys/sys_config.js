var table;
$(function () {
    layui.use('table', function () {
        table = layui.table;
        //第一个实例
        table.render({
            url:  config.baseUrl + 'sysConfig/list',
            cols: [[ //表头
                {type: "checkbox", fixed: 'left'},
                    {field: 'configKey', title: 'key值'},
                    {field: 'configValue', title: 'value值'},
                    {field: 'remark', title: '备注'},
                    {field: 'creator', title: '配置人'},
                    {field: 'createTime', title: '配置时间'}
            ]]
        });
    });
});
var vm = new Vue({
    el: '#container',
    data: {
        q: {},
        showList: true,
        title: null,
        sysConfig: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.sysConfig = {};
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            var url = vm.sysConfig.id == null ? "sysConfig/save" : "sysConfig/update";
            $.ajax({
                type: "POST",
                url: config.baseUrl + url,
                data: JSON.stringify(vm.sysConfig),
                success: function (r) {
                    if (r.code === 1) {
                        layer.alert("操作成功", function (index) {
                            layer.close(index);
                            vm.reload();
                        });
                    } else {
                        alert(r.message);
                    }
                }
            });
        },
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            layer.confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: config.baseUrl + "sysConfig/delete",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 1) {
                            layer.alert("删除成功", function (index) {
                                layer.close(index)
                                vm.reload();
                            });
                        } else {
                            layer.alert(r.message);
                        }
                    }
                });
            });
        },
        getInfo: function (id) {
            $.get(config.baseUrl + "sysConfig/info/" + id, function (r) {
                if (r.code == 1) {
                    vm.sysConfig = r.data;
                } else {
                    layer.alert(r.message);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            table.reload('tableData', {
                where: vm.q,
                page: {
                    curr: 1 //重新从第 1 页开始
                }
            });

        }
    }
});
