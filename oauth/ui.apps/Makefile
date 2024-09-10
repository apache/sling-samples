css: src/main/content/jcr_root/content/oauth-demo/static/css/output.css
.PHONY=css

src/main/content/jcr_root/content/oauth-demo/static/css/output.css: src/main/frontend/input.css tailwind.config.js src/main/content/jcr_root/apps/oauth-demo/components/welcome/welcome.html src/main/content/jcr_root/apps/oauth-demo/components/youtube-videos/show.html
	tailwindcss --input $< --output $@
