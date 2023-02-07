module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
    cssnano: process.env.NODE_ENV == 'prod' ? {} : false
  }
}
