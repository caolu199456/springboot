var vm = new Vue({
    el: '#vueContainer',
    data: {
        //一级菜单下有二级菜单
        menuData:[],
        loginData:JSON.parse(sessionStorage.getItem("loginData"))
    },
    methods: {
        logout:function () {
            $.ajax({
                url:config.baseUrl+"sys/logout",
                type:"post",
                success:function (r) {
                    if (r.code == 1) {
                        sessionStorage.removeItem("loginData");
                        window.location.href = config.webUrl + "login.html";
                        return;
                    }
                }
            })
        },
        updatePwdClick:function () {
            var index = layer.open({
                type: 2,
                area: ['900px', '500px'],
                fix: false, //不固定
                maxmin: true,
                shadeClose: true,
                shade:0.4,
                title: "修改密码",
                content: config.webUrl+"views/update_pwd.html"
            });
        }

    },
    created:function () {
        $.ajax({
            url:config.baseUrl + "sys/getMenuList",
            success:function (r) {
                if (r.code == 1) {
                    vm.menuData = r.data;
                    layui.use('element', function() {
                        var element = layui.element;
                        element.init();
                    });
                }else {
                    layer.alert(r.message);
                }
            }
        })
    }
});