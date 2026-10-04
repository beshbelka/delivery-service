const gulp = require('gulp');
const fileInclude = require('gulp-file-include');
const browserSync = require('browser-sync').create();

const HTML_SRC = [
    'src/**/*.html',
    '!src/partials/**'
];
const STATIC_SRC = 'static/**/*';
const DEST = 'dist/';

function html() {
    return gulp.src(HTML_SRC)
        .pipe(fileInclude({
            prefix: '@@',
            basepath: '@file'
        }))
        .pipe(gulp.dest(DEST))
        .pipe(browserSync.stream());
}

function assets() {
    return gulp.src(STATIC_SRC)
        .pipe(gulp.dest(DEST))
        .pipe(browserSync.stream());
}

const build = gulp.series(html, assets);

function serve(done) {
    browserSync.init({
        server: { baseDir: './dist' },
        open: false,
        notify: false,
        port: 3000
    });
    done();
}

function watch() {
    gulp.watch(['src/**/*.html'], html);
    gulp.watch(STATIC_SRC, assets);
}

exports.build = build;
exports.watch = gulp.series(build, serve, watch);
exports.default = build;