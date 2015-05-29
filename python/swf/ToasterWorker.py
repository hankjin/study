from BaseWorker import BaseWorker

class ToasterWorker(BaseWorker):
    def activity(self, context, task):
        toaster = Toaster()
        hostname = context.get_hostname()
        code,status = toaster.check(hostname)
        print 'Toaster result %s:%d:%s' % (hostname, code, status)
        context.set_attr('Toaster', status)
        if 0 == Code:
            return 'Succeed'
        else:
            return 'Fail'

if __name__ == '__main__':
    ToasterWorker().run(verbose=True)
