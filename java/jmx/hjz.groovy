import javax.management.remote.*
import javax.management.*
import groovy.jmx.builder.*

class Sample {
    def main() {
        def connection = new JmxBuilder().client(port: 8090, host: 'localhost')
        connection.connect()
        def server = connection.MBeanServerConnection

        def bean = new GroovyMBean(server, "xman:type=SysConfig")
        bean.listAttributeNames().each{name ->
            def attrDescription = bean.describeAttribute(name)
            println "Attribute: ${attrDescription}"
        }
        bean.listOperationNames().each{name ->
            bean.describeOperation(name).each{desc->
                println "Operation: ${desc}"
            }
        }
        println bean.sum(2,5)
        return bean.doConfig()
    }
}
