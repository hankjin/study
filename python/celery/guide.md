Running
1. Create tasks.py
cat > tasks.py <<EOF
from celery import Celery

app = Celery('tasks',
        backend='redis://localhost',
        broker='sqs://AKIAJ3MAZH5I3YICN6TA:HoxSvgXzFwu7jEPOhiyX0uFFTKzATkRms7eoUkoy@')

@app.task
def add(x, y):
    return x + y
EOF

2. Start worker: celery -A tasks worker --loglevel=info

3. Test task
>>> from tasks import add
>>> result = add.delay(4,4)
>>> result.ready()
True
>>> result.get()
8

