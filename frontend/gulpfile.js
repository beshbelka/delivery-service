const gulp = require('gulp');
const fileInclude = require('gulp-file-include');

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
        .pipe(gulp.dest(DEST));
}

function assets() {
    return gulp.src(STATIC_SRC)
        .pipe(gulp.dest(DEST));
}

const build = gulp.series(html, assets);

function watch() {
    gulp.watch(['src/**/*.html'], html);
    gulp.watch([STATIC_SRC], assets);
}

exports.build = build;
exports.watch = watch;
exports.default = build;