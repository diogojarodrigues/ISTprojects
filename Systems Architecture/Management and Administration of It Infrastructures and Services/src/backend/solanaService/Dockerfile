FROM node:20
# Create app directory
WORKDIR /usr/src/app
# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY . .
RUN npm install
# If you are building your code for production
# RUN npm ci --omit=dev
# Bundle app source
EXPOSE 80
CMD [ "node", "server.js" ]

ENV DB_USER=crypto
ENV DB_PASSWORD=password
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=crypto