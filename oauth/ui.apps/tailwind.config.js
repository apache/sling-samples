/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/content/jcr_root/**/*.{html,js}"],
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}

