# jabub
Universal migration tool


1. docker build -t jabub-jdk-python-js-base -f ./BaseDockerfile .

2. docker run -v ${PWD}:/scripts jabub-jdk-python-js-base /scripts/ScriptPython
3. docker run -v ${PWD}:/scripts jabub-jdk-python-js-base /scripts/ScriptJava
4. docker run -v ${PWD}:/scripts jabub-jdk-python-js-base /scripts/ScriptJs
5. docker run -v ${PWD}:/scripts jabub-jdk-python-js-base /scripts/ScriptBash

!!! on mac you may need to replace ${PWD} with $(pwd) or 