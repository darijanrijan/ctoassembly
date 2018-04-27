# C to Assembly - Compiler and Interpreter

http://ctoassembly.com

C to Assembly is an online compiler of micro-C code (a subset of C large enough for demonstration) and interpreter of hypothetical Assembly (micro-C is compiled to hypothetical Assembly which looks a lot like x86 asm).

See docs here:
  - [micro-C](http://ctoassembly.com/microc.html)
  - [hypothetical Assembly](http://ctoassembly.com/asm.html)

## Repo structure
* `src/main/java/com/ctoassembly/compiler` - Java compiler (micro-C to hypothetical-asm)
   * *Yeah, yeah, yeah, I am not joking - Java is compiling c to asm.*
   * `mycc.lex` - scanner written in Backus-Naur form.
   * `mycc.cup` - LR parser that defines the grammar. All the code generation is executed from here (when a rule is matched), so it's a good starting point for reading code.
* `WebContent` - JavaScript/Angular assembly interpreter 
   * `ccode.html` - compiling happens here.
   * `js/angular/controller.js` - angular controller backing up the ccode.html. It calls the servlet holding the compiler, presents the asm code and interprets (executes) it line by line.
   * `js/cpu.js` -  that's the javascript CPU
   * `js/asmmetamodel.js` - all asm instructions and their execution strategy is encapsulated here
   * `js/ui.js` - ui support (drawing and such)
   * `js/constants.js` - CPU constants
   * `js/util.js` - util support for everything above
   * `js/prototype.js` - useful javascript extensions that I used throughout the code



### Todos

 - Enlarge the subset of C that the compiler can compile
 - Fix bugs

License
----

Apache 2.0


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
