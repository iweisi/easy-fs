// 配置layui模块所在的位置
layui.config({
    base: 'assets/module/'
}).extend({
    formSelects: 'formSelects/formSelects-v4'
}).use(['jquery', 'layer'], function () {
    var $ = layui.jquery;
    var layer = layui.layer;

});

