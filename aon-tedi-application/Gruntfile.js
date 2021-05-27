module.exports = function(grunt) {
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    copy: {
      main: {
        files: [
          {
            expand: true,
            cwd: 'src',
            src: 'tedi-gmail/**/*.json',
            dest: 'lib/',
            flatten: false,
            filter: 'isFile',
          },
          {
            expand: true,
            cwd: 'src',
            src: 'tedi-gmail/**/*.tmpl',
            dest: 'lib/',
            flatten: false,
            filter: 'isFile',
          },
        ],
      },
    },
    clean: {
      folder: ['./test/pdfs'],
    },
  });
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.registerTask('default');
};
