var table,zTreeObj;
$(function () {
    layui.use('table', function () {
        table = layui.table;
        //第一个实例
        table.render({
            url:  config.baseUrl + 'sysRole/list',
            cols: [[ //表头
                {type: "checkbox", fixed: 'left'},
                    {field: 'id', title: 'ID'},
                    {field: 'roleCnName', title: '角色中文名'},
                    {field: 'roleName', title: '角色英文名'},
                    {field: 'status', title: '状态',templet:function (item) {
                            if (item.status==1){
                                return "<button class='layui-btn layui-btn-sm layui-bg-blue'>启用</button>"
                            }else {
                                return "<button class='layui-btn layui-btn-sm layui-bg-red'>禁用</button>"
                            }
                    }},
                    {field: 'createTime', title: '创建时间'},
                    {field: 'updateTime', title: '修改时间'},
            ]]
        });
    });
});
var canUsefulMenus = [];
var vm = new Vue({
    el: '#container',
    data: {
        q: {},
        showList: true,
        title: null,
        sysRole: {},
        settings:{
            data: {
                simpleData: {//简单数据模式
                    enable: true,
                    idKey: "id",
                    pIdKey: "parentId",
                    rootPId: 0
                }
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: { "Y": "ps", "N": "s" }
            },
            callback:{
                onClick:function (e) {
                    e.preventDefault();
                }
            }
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.sysRole = {"status":1,roleMenuList:canUsefulMenus};
            zTreeObj = $.fn.zTree.init($("#zTree"), vm.settings,canUsefulMenus);
            zTreeObj.expandAll(true);

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
            var url = vm.sysRole.id == null ? "sysRole/save" : "sysRole/update";
            var ids = [];
            this.getAllSelectNodeIds(zTreeObj.getNodes(),ids);

            var roleMenus = [];
            if (ids && ids.length > 0) {
                for (var i = 0; i < ids.length; i++) {
                    roleMenus[i] = {id: ids[i]}
                }
            }
            vm.sysRole.roleMenuList = roleMenus;

            if (isEmpty(vm.sysRole.roleName)){
                layer("角色名称不能为空");
                return;
            }
            $.ajax({
                type: "POST",
                url: config.baseUrl + url,
                data: JSON.stringify(vm.sysRole),
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
                    url: config.baseUrl + "sysRole/delete",
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
            $.get(config.baseUrl + "sysRole/info/" + id, function (r) {
                if (r.code == 1) {
                    vm.sysRole= r.data;

                    zTreeObj = $.fn.zTree.init($("#zTree"), vm.settings, r.data.roleMenuList);
                    zTreeObj.expandAll(true);

                } else {
                    layer.alert(r.message);
                }
            });
        },
        givePermissions: function (id) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var postData = {};
            postData.roleId = id;

        },
        reload: function () {
            vm.showList = true;
            table.reload('tableData', {
                where: vm.q,
                page: {
                    curr: 1 //重新从第 1 页开始
                }
            });

        },
        getAllSelectNodeIds:function (nodes,ids) {
            if (!nodes) {
                return;
            }
            if (nodes && nodes.length>0){
                for (var i = 0; i < nodes.length; i++) {
                    if (nodes[i].checked) {
                        ids.push(nodes[i].id)
                    }
                    if (nodes[i].children && nodes[i].children.length > 0) {
                        this.getAllSelectNodeIds(nodes[i].children,ids);
                    }

                }
            }
        }
    },
    created:function () {
        $.ajax({
            url: config.baseUrl + "sysMenu/queryCanUsefulMenus",
            success:function (r) {
                if (r.code == 1) {
                    canUsefulMenus =  r.data;
                    zTreeObj = $.fn.zTree.init($("#zTree"), vm.settings,canUsefulMenus);
                    zTreeObj.expandAll(true);
                }else{
                    layer.alert(r.message);
                }
            }
        })
    }
});
