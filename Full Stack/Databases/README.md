# Setup

You need to have docker installed on your machine. You can find instructions on how to install docker [here](https://docs.docker.com/get-docker/).

### Before we run our App we need to setup our enviroment.


#### 1. Run the following command to build the docker image.

```bash
$ docker compose up --build
```  


#### 2. Find the section of the logs towards the bottom of the Terminal window that look like this excerpt:

```log
db-workspace-notebook-1  |     Or copy and paste one of these URLs:
db-workspace-notebook-1  |         http://7fd8c38e99bd:8888/lab?token=f83ee982668ebe66bee2dbeb5875d14131a1d118d1e0fa12
db-workspace-notebook-1  |         http://127.0.0.1:8888/lab?token=f83ee982668ebe66bee2dbeb5875d14131a1d118d1e0fa12
```

And click on the second link to open the Jupyter Notebook.  


#### 3. Open a terminal on the notebook and run the following command to create the database and the user.

```bash
$ psql -h postgres -U postgres
```

Use the password "postgres" when prompted.

```sql
\i work/delivery-3/schema.sql
\i work/delivery-3/populate.sql
\i work/delivery-3/ICs.sql
\q
```  


#### 4. Rerun the following command to restart the docker image and open the app on the "localhost:5001".

```bash
$ docker compose up --build
``` 


### You have all set up! Enjoy!
