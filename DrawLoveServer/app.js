var express = require('express');
var mongoose = require('mongoose');
var assert = require('assert');
var session = require('express-session');
var bodyParser  = require('body-parser');
var http = require('http');
var path = require('path');
var sass = require('node-sass-middleware');

var config = require('./config.js');
var app = express();

//-- connect to database
var mongoose = require('mongoose');
mongoose.connect("mongodb://localhost/drawlove-db", function(error){
	if (error){
		console.log('Error: Cannot connect to database. App exits.');
		console.log(error);
		process.exit(1);
	}
});

//-- middleware init
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(session({
	secret: "-this-will-not-be-shown-3141592654-",
	cookie: {maxAge: 600000},
	saveUninitialized: false,
	resave: true
}));
app.set('views', path.join(__dirname, 'www', 'app', 'views'));
app.set('view engine', 'pug');
app.use(
	sass({
        src: path.join(__dirname, 'www', 'app', 'assets', 'scss'),
        dest: path.join(__dirname, 'wwww', 'public', 'css'),
        prefix: '/assets/css'
    })
);
app.use('/', express.static(path.join(__dirname, 'www', 'public')));

//-- routing
app.use('/', require('./routes/web-routes.js'));
app.use('/api', require('./api/main-routes.js'));
app.use('/api/user', require('./api/user-routes.js'));
app.use('/api/group', require('./api/group-routes.js'));

var httpServer = http.createServer(app);
httpServer.listen(config.port);
