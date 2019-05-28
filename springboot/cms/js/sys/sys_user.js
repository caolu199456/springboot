var table;
$(function () {
    //config.js已经设置默认参数
    layui.use('table', function () {
        table = layui.table;
        //第一个实例
        table.render({
            url: config.baseUrl + 'sysUser/list',  //数据接口
            cols: [[ //表头
                {type: "checkbox", fixed: 'left'},
                {field: 'id', title: 'ID', sort: true},
                {field: 'account', title: '账号'},
                {field: 'username', title: '用户姓名'},
                {field: 'status', title: '状态',
                    templet:function (item) {
                        if (item.status==1){
                            return "<button class='layui-btn layui-btn-sm layui-bg-blue'>启用</button>"
                        }else {
                            return "<button class='layui-btn layui-btn-sm layui-bg-red'>禁用</button>"
                        }
                }},
                {field: 'updateTime', title: '修改时间'},
                {field: 'createTime', title: '创建时间'}
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
        sysUser: {},
        allRoles:[],
        //用户拥有的id
        userRoleIds:[]

    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.sysUser = {"status":1};
            vm.userRoleIds = [];
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }

            vm.title = "修改";
            vm.showList = false;
            vm.userRoleIds = [];

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            var url = vm.sysUser.id == null ? "sysUser/save" : "sysUser/update";
            var userRoleList = [];
            if (vm.userRoleIds) {
                for (var i = 0;i<vm.userRoleIds.length;i++){
                    userRoleList.push({id: vm.userRoleIds[i]});
                }
            }
            vm.sysUser.userRoleList = userRoleList;
            if (isEmpty(vm.sysUser.account)){
                layer.alert("账号不能为空");
                return;
            }
            $.ajax({
                type: "POST",
                url: config.baseUrl + url,
                data: JSON.stringify(vm.sysUser),
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
                    url: config.baseUrl + "sysUser/delete",
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
        resetPwd: function () {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            layer.confirm('确定要重置选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: config.baseUrl + "sysUser/resetPwd",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 1) {
                            layer.alert("重置成功", function (index) {
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
            $.get(config.baseUrl + "sysUser/info/" + id, function (r) {
                if (r.code == 1) {
                    vm.sysUser = r.data;
                    if (vm.sysUser.userRoleList) {
                        for (var i = 0; i < vm.sysUser.userRoleList.length; i++) {
                            vm.userRoleIds.push(vm.sysUser.userRoleList[i].id);
                        }
                    }
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
    },
    created:function () {
        $.ajax({
            url:config.baseUrl+"sysRole/queryAllRoles",
            success:function (r) {
                if (r.code == 1) {
                    vm.allRoles = r.data;
                }
            }
        })
    }
});
