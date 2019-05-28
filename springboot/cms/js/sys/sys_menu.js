var form;

var vm = new Vue({
    el: '#container',
    data: {
        q: {},
        showList: true,
        title: "新增",
        sysMenu: {},
        settings:{
            data: {
                simpleData: {//简单数据模式
                    enable: true,
                    idKey: "id",
                    pIdKey: "parentId",
                    rootPId: 0
                }
            },
            callback: {
                onClick: function (event, treeId, treeNode) {
                    event.preventDefault();

                    $.ajax({
                        url: config.baseUrl + "sysMenu/info/" + treeNode.id,
                        success: function (r) {
                            if (r.code == 1) {
                                vm.title = "修改";
                                vm.sysMenu = r.data;
                                if (treeNode.getParentNode()) {
                                    var parentName = treeNode.getParentNode().name;
                                    vm.sysMenu.parentName = parentName;
                                }else {
                                    vm.sysMenu.parentName = "无父级菜单";
                                }
                                //由于layer重写了select 所以要手动刷新组件
                            } else {
                                layer.alert("错误");
                            }
                        }
                    })
                }
            }
        }

    },
    methods: {
        query: function () {
        },
        add: function () {
            if (!this.sysMenu.id) {
                layer.alert("请选择一个节点");
            }
            if (this.sysMenu.level>=3) {
                layer.alert("最多到第三层");
            }
            vm.title = "新增";
            //点击前的数据
            var parentLevel = this.sysMenu.level;
            var parentId = this.sysMenu.id;
            var parentName = this.sysMenu.name;

            var postData = {};
            postData.parentId = parentId;
            postData.parentName = parentName;
            if (parentLevel == 0) {
                //根目录现在添加一级菜单
                postData.level = 1;
            }else if (parentLevel == 1) {
                //添加二级菜单
                postData.level = 2;
            }else if (parentLevel == 2) {
                //添加按钮
                postData.level = 3;
            }
			postData.icon="点击选择";
            this.sysMenu = postData;
            this.sysMenu.status = 1;//默认启用
        },
        saveOrUpdate: function () {

            var url = config.baseUrl + "sysMenu/save";
            if (this.sysMenu.id) {
                url = config.baseUrl + "sysMenu/update"
            }
            if (!this.sysMenu.parentId) {
                layer.alert("请选择一个父级菜单");
                return;
            }
            $.ajax({
                url: url,
                type:"post",
                data: JSON.stringify(vm.sysMenu),
                success: function (r) {
                    if (r.code == 1) {
                        layer.alert("操作成功",function (index) {
                            layer.close(index);
                            vm.reload();
                        });
                    } else {
                        layer.alert(r.message);
                    }
                }
            });
        },
        iconInputClick: function () {
            var index = layer.open({
                type: 2,
                area: ['900px', '600px'],
                fix: false, //不固定
                maxmin: true,
                shadeClose: true,
                shade:0.4,
                title: "选择图标",
                content: config.webUrl+"icon.html"
            });
        },
        reload:function () {
            $.ajax({
                url: config.baseUrl + "sysMenu/list",
                success:function (r) {
                    if (r.code == 1) {
                        zTreeObj = $.fn.zTree.init($("#zTree"), vm.settings, r.data);
                        // zTreeObj.expandAll(true);
                        var nodes = zTreeObj.getNodesByParam("level", 1);
                        for (var i = 0; i < nodes.length; i++) {
                            zTreeObj.expandNode(nodes[i], true, false, false);
                        }
                    }else{
                        layer.alert(r.message);
                    }
                }
            })
        }
    },
    mounted:function () {
    }
});

function setLayerIconValue(obj) {
    //子窗口调用
    vm.sysMenu.icon = obj.icon;
}
vm.reload();