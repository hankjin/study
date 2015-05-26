#!/bin/env python
import sys
import os
import states

class TicketAPI:
    def list_tickets(self, hostname):
        # TODO
        return []
    def resolve_ticket(self, ticket):
        # TODO
        return True
    def cut_ticket(self, hostname, status):
        # TODO
        return 'id'

class BaseToaster:
    # TODO: call basic toaster api
    def get_status(self):
        pass
    def is_ssh_able(self, hostname):
        pass

class Toaster:
    def check(self, hostname):
        # Run Toaster:TODO
        outputs = []
        return 0,'\n'.join(outputs)

class Rebooter:
    def __init__(self):
        pass
    def reboot(self, hostname):
        #TODO
        pass

class RemoteCommand:
    def run(self, hostname, command):
        cmd = "ssh -n %s '%s'" % (hostname, command)
        return self.run(cmd)
    def run(self, command):
        tmp_file = '/tmp/hjz'
        cmd = "%s > %s" % (command, tmp_file)
        ret = os.system(cmd)
        output = open(tmp_file).read()
        return ret,output

class VIPUtil:
    def get_hw(self, hostname):
        # TODO: ssh -n $hostname "cat /etc/'
        return ''
    def get_vip_num(self, hostname):
        # TODO: ssh snuffy "showvips.pl"
        return 2
    def is_ok(self, hostname):
        hw = self.get_hw(hostname)
        vip_num = self.get_vip_num(hostname)
        vip_rule = {
                'EC2.': 3,
                'Slimline': 2,
                }
        if not hw in vip_rule:
            print 'Unknown HW %s' % hw
            return False
        if vip_rule[hw] != vip_num:
            print 'HW %s has %d but expect %d' % (
                    hw,
                    vip_num,
                    vip_rule[hw])
            return False
        return True

if __name__ == '__main__':
    if len(sys.argv) == 1:
        print 'Usage: %s [dot|validate]' % sys.argv[0]
        sys.exit(1)
    if 'validate' in sys.argv:
        states.validate()
    if 'dot' in sys.argv:
        states.generate_dot(open('states.dot','w'))
        os.system('dot -Tpng -o states.png states.dot')
