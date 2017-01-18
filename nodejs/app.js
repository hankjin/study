var express = require('express');
var app = express();
var qr = require('qr-image');
var btoa = require('btoa');

var baseUrl = 'http://localhost:8080/';
app.get('/', function(req, res) {
    var url = baseUrl + '?id=' + req.query.id;
    var img = qr.imageSync(url, {type:'svg'});
    var svg64 = btoa(img);
    var image64 = 'data:image/svg+xml;base64,' + svg64;
    res.writeHead(200, {'Content-Type':'text/html; charset=utf-8;'});
    res.write('<div style="display:none;">');
    res.write('<img id="source" src="'+image64+'"/>');
    res.write('</div>');
    res.write('<canvas id="canvas" width="600" height="800"></canvas>');
    res.write('<script>');
    res.write('var canvas = document.getElementById("canvas");');
    res.write('var ctx = canvas.getContext("2d");');
    res.write('ctx.font="30px Arial";ctx.fillText("体 检 报 告 单", 160, 40);');
    res.write('ctx.beginPath();ctx.moveTo(40, 80);ctx.lineTo(520, 80);ctx.stroke();');
    res.write('var image = document.getElementById("source");');
    res.write('ctx.drawImage(image, 380, 380, 80, 80);');
    res.end('</script>');
});


app.listen(8080);
