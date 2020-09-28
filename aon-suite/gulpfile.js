var gulp = require('gulp');

// Requires browser-sync for live reloading the browser and creates a Browsersync instance
var browserSync = require('browser-sync').create();

// Requires the gulp-sass plugin
var sassPlugin = require('gulp-sass');
var concat = require('gulp-concat');

// Watch files for changes
function watchFiles() {
    gulp.watch('assets/scss/*.scss', sass);
    gulp.watch('assets/js/*.js', scripts);
    gulp.watch('public/js/*.js').on('change', browserSync.reload); // Triggers a full reload on the brower
    gulp.watch('public/**/*.html').on('change', browserSync.reload); // Triggers a full reload on the brower
}

// Compiles Sass to CSS
function sass() {
    return gulp.src('assets/scss/app.scss')
        .pipe(sassPlugin()) // Transforms Sass to CSS using the gulp-sass plugin
        .pipe(gulp.dest('public/css'))
        .pipe(browserSync.stream()); // Stream changes to the browser (without a full reload)
}

// Añadir al array los módulos JS para que se compilen todos juntos
function scripts() {
    return gulp.src([
        'node_modules/jquery/dist/jquery.js',
        'node_modules/@ckeditor/ckeditor5-build-classic/build/ckeditor.js',
        'node_modules/@ckeditor/ckeditor5-build-classic/build/translations/es.js',
        'node_modules/bootstrap/dist/js/bootstrap.bundle.min.js',
        'assets/js/app.js'
    ])
    .pipe(concat('app.js'))
    .pipe(gulp.dest('public/js/'))
    .pipe(browserSync.stream()); // Stream changes to the browser (without a full reload)
    //.pipe(rename({suffix: '.min'})) // Cuando vayamos a poner el .min
    //.pipe(uglify())
}

function sync(done) {
    browserSync.init({
        server: {
            baseDir: ['./', './public'] // Base directories exposed to the server
        },
        port: 9000, // Port used by the app
        ui: {
            port: 9001 // Port used by the Browsersync UI
        },
        listen: 'localhost', // Comment this line to get an external URL, which can be accessed from other devices on the same network
        notify: false // If true, shows a notificacion saying Browsersync is connected
    });

    done();
}

// First compiles Sass to CSS if there are changes, then starts watching files and Browsersync
var watch = gulp.series(sass, scripts, gulp.parallel(watchFiles, sync));

exports.sass = sass;
exports.scripts = scripts;
exports.watch = watch;
