# web-ide-front/Dockerfile
# Stage 1: Build the React app
FROM node:20-alpine as build

WORKDIR /app

COPY package.json ./
COPY package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Serve the React app with Nginx
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
