#!/bin/env python

import os
import time
from optparse import OptionParser
from threading import Thread
from BaseDecider import BaseDecider
import states

class WorkerThread(Thread):
    def __init__(self, worker, task, opts):
        self.worker = worker
        self.task = task
        self.opts = opts
        Thread.__init__(self)
        pass
    def run(self):
        self.worker.handle_task(self.task, self.opts.verbose)

class DeciderThread(Thread):
    def __init__(self, opts):
        self.running = True
        self.opts = opts
        Thread.__init__(self)
        pass
    def stop(self):
        self.running = False
        pass
    def run(self):
        while self.running:
            BaseDecider().run(verbose = self.opts.verbose)

class PollThread(Thread):
    def __init__(self, cls, opts):
        self.running = True
        self.cls = cls
        self.workers = []
        self.opts = opts
        Thread.__init__(self)
    def stop(self):
        self.running = False
    def run(self):
        while self.running:
            worker = self.cls()
            task = worker.poll_task(self.opts.verbose)
            if None == task:
                continue
            print 'Got Worker %s' % self.cls.__name__
            worker_thread = WorkerThread(worker, task, self.opts)
            worker_thread.setDaemon()
            worker_thread.start()
            for thread in self.workers:
                if not thread.is_alive():
                    self.workers.remove(thread)
            workers.append(worker_thread)
            pass
        print 'Exiting %s' % self.cls.__name__
        for worker in self.workers:
            while worker.is_alive():
                print 'One live thread to wait'
                time.sleep(10)
        print 'Exited %s' % self.cls.__name__
class SWFRunner:
    def __init__(self):
        self.flag_file='shutdown'
        self.threads = []
    def get_opts(self):
        parser = OptionParser()
        parser.add_option('-w', '--workers', dest='workers',
                action='store', default='',
                help = 'Start workers[all|PingWorker,XWorker]')
        parser.add_option('-d', '--decider', dest='decider',
                action='store_true',
                help = 'Start decider')
        parser.add_option('-v', '--verbose', dest='verbose',
                action='store_true', default=False,
                help='Print debug logs')
        self.opts,args = parser.parse_args()
    def main(self):
        self.get_opts()
        self.begin_running()
        if self.opts.decider:
            self.start_decider()
        if self.opts.workers:
            self.start_workers()
        while self.is_running():
            print 'running ...'
            time.sleep(20)
            pass
        self.stop()
    def begin_running(self):
        if not self.is_running():
            os.unlink(self.flag_file)
        pass
    def is_running(self):
        return not os.path.isfile(self.flag_file)
    def start_decider(self):
        thread = DeciderThread(self.opts)
        thread.start()
        self.threads.append(thread)
    def start_workers(self):
        workers = [a.replace('Activity','Worker') for a in states.Activities if a.endswith('Activity')]
        # start poll threads to poll task for each kind of worker
        for worker_name in workers:
            if ( not 'all' in self.opts.worker ) and (not worker_name in self.opts.worker):
                continue
            mod = __import__(worker_name) # import PingWorker
            cls = getattr(mod, worker_name) # PingWorker.PingWorker
            thread = PollThread(cls, self.opts)
            thread.start()
            self.threads.append(thread)
            pass
        pass
    def stop(self):
        # wait for poll threads to exit
        print 'Shutting down'
        for thread in self.threads:
            thread.stop()
        for thread in self.threads:
            thread.join()

if __name__ == '__main__':
    runner = SWFRunner()
    runner.main()

