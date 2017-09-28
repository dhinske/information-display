# Information-Display
A Service-/Database-less approach to keep track of any information you would like to display. Java needs to be installed on the target machine.

![alt text](https://github.com/dhinske/information-display/tree/master/src/main/resources/example-display.png "Example")

The structure of the index.html is a simple table:
```
  <table class="container"> 
   <thead> 
    <tr> 
     <th>Key1</th> 
     <th>Key2</th> 
     <th>Key3</th>
    </tr> 
   </thead> 
   <tbody>
    <tr> 
     <td>value_key1</td> 
     <td>value_key2</td> 
     <td>value_key3</td>
    </tr>
    ...
   </tbody> 
  </table>  
```

## How to use
1. Copy the index.html, style.css (of your choice) and the assembled build-jar (target/information-display.jar) into your desired location
2. Update the index.html on the machine via the jar-file
* java -jar {PATH}/information-display.jar ...
* {key} {column} {value} - Will replace the entry in column {key} and row {column} with {value}
* addColumn {name} - adds an table-head entry and an empty body entry for all existing rows
* removeColumn {name} - deletes the table-head {name} and all body entries from the table
* removeRow {value_key} - deletes the table-body with the key {value_key}. The key has to be in the first column (value_key1 in the example).


## Update via Jenkins Global Pipeline Library
- Add a new file under vars of your global Jenkins pipeline library (f.e. updateDisplay.groovy)
- paste content

```
def call(String host, String credentials, String path, String name, String key, String value) {
	sshagent(credentials: [credentials]) {
		sh "ssh -o StrictHostKeyChecking=no -l jenkins " + host + " 'sudo java -jar "+path+"/information-display.jar "+name+" "+key+" "+value+"'"
	}
}
```

- call method from your pipeline like
```
updateDisplay("host.company.de", "jenkins_ssh", "/usr/share/nginx/html", "serviceName", "Port", "1234")
```