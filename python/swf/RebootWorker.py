from BaseWorker import BaseWorker

class RebootWorker(BaseWorker):
    # TODO
    TMO = 20 * 60 # 20 min
    def activity(self, context, task):
        hostname = context.get_hostname()
        rebooter = Rebooter()
        toaster = BaseToaster()
        for i in range(RETRY):
            rebooter.reboot(hostname)
            time.sleep(TMO)
            if toaster.is_ssh_able(hostname):
                return 'Succeed'
        return 'Fail'
