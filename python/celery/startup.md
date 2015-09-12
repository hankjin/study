Analyze how celery worker works
celery -A tasks worker --loglevel=info

1. celery wrapper /usr/local/bin/celery
#!/usr/bin/python
# EASY-INSTALL-ENTRY-SCRIPT: 'celery==3.2.0a2','console_scripts','celery'
__requires__ = 'celery==3.2.0a2'
import sys
from pkg_resources import load_entry_point

if __name__ == '__main__':
    sys.exit(
        load_entry_point('celery==3.2.0a2', 'console_scripts', 'celery')()
    )
    
2. entry_point.txt /Library/Python/2.7/site-packages/celery-3.2.0a2-py2.7.egg/EGG-INFO/entry_points.txt 
[console_scripts]
celery = celery.__main__:main
celeryd = celery.__main__:_compat_worker
celeryd-multi = celery.__main__:_compat_multi
celerybeat = celery.__main__:_compat_beat

3. /Library/Python/2.7/site-packages/celery-3.2.0a2-py2.7.egg/celery/__main__.py

4. /Library/Python/2.7/site-packages/celery-3.2.0a2-py2.7.egg/celery/bin/celery.py main()

5. CeleryCommand.execute_from_commandline(argv)

5.1 celery.app.utils.find_app

6. /Library/Python/2.7/site-packages/celery-3.2.0a2-py2.7.egg/celery/bin/worker.py
208         worker = self.app.Worker(
209             hostname=hostname, pool_cls=pool_cls, loglevel=loglevel,
210             logfile=logfile,  # node format handled by celery.app.log.setup
211             pidfile=self.node_format(pidfile, hostname),
212             state_db=self.node_format(state_db, hostname), **kwargs
213         )
214         worker.start()
celery.concurrency.prefork.TaskPool

7. self.app = celery.app.base.Celery

8. Celery.subclass_with_self(celery.apps.worker:Worker)

9. worker.start
9.1 on_before_init(called by work.WorkController.init)
9.1.1 setup_defaults(imp and called by work.WorkController.init)
a. select_queues
b. concurrenty = cpu_count
c. use_eventloop = true
d. blueprint
9.2 on_after_init(called by work.WorkController.init)
9.3 on_init_blueprint(called by work.WorkController.init->setup_instance)
9.4 on_start
a. create_pidlock(WorkControler)
b. purge_messages if self.purge
c. install_platform_tweaks
9.5 on_consumer_ready
9.6 purge_messages()
9.7 tasklist
9.8 extra_info
9.9 startup_info
9.a install_platform_tweaks

