/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@GrabResolver(name='ukp-oss-snapshots',
     root='http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-snapshots')
@Grab('org.dkpro.script:dkpro-script-groovy:0.2.0-SNAPSHOT')
import groovy.transform.BaseScript
import org.dkpro.script.groovy.DKProCoreScript;
@BaseScript DKProCoreScript baseScript

version "${version}"

// Debug output
println "${type}"
println args;

// Mapping of parameters to their types so we can cast the values before passing them to uimaFIT
def typemap = [:];
<%
// BEGIN TEMPLATE LOGIC
def paramDecls;
if (type == "reader") {
    paramDecls = format.readerSpec.metaData.configurationParameterDeclarations
        .configurationParameters.sort { it.name };
} else {
    paramDecls = format.writerSpec.metaData.configurationParameterDeclarations
        .configurationParameters.sort { it.name };
}

paramDecls.each { param ->
    println "typemap[\"${param.name}\"] = ${param.type};";
}
// END TEMPLATE LOGIC
%>

// Parse the command line arguments
def input = args[0]
def output = args[1]
def hideOut = args[2]
def paramList = [:];

if (args.length < 3){
    println "Not enough params";
    System.exit(1);
}

for (pos = 3; pos < args.length; pos += 2) {
    def paramName = args[pos].substring(1);
    paramList[paramName] = args[pos+1].asType(typemap[paramName]);
}

// Assemble the actual pipeline
<%
// BEGIN TEMPLATE LOGIC
if(type=="reader"){
// END TEMPLATE LOGIC
%>
read "${format.name}" from input params(paramList)

write 'BinaryCas' to output params([
	overwrite: true,
	singularTarget: true])
<%	
// BEGIN TEMPLATE LOGIC
} else {
// END TEMPLATE LOGIC
%>
paramList["singularTarget"] = true

read 'BinaryCas' from input

write "${format.name}" to output params(paramList)

<%
// BEGIN TEMPLATE LOGIC
}
// END TEMPLATE LOGIC
%>