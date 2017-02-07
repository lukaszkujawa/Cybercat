grammar Cybercat;

parse
    :   stmt_list EOF # statementList
    ;

stmt_list
    :   stmt* # statement
    ;

stmt
    :   ID '=' expr (NEWL | EOF) # assign
    |   ID '(' ')' # invokeByReference
    |   ID '(' value (',' value)? ')' # invokeByReferenceArgs
    |   NEWL # newline
    |   map_value '=' expr (NEWL | EOF) # assignMapValue
    |   PRINT expr (NEWL | EOF) # print
    |   ID STRING mapdef (NEWL | EOF) # moduleDefinition
    |   expr (NEWL | EOF) # expression
    |   RETURN value (NEWL | EOF) # returnValue
    ;

callMethodChain
    :   callMethod ('.' callMethod )* # invokeMethodChain
    ;

callMethod
    :   module_method '(' ')' # invokeModuleMethod
    |   module_method '(' value (',' value)? ')' # invokeModuleMethodArgs
    ;

module_method
    :   ret_variable ( '.' ID )* # moduleMethod
    ;

list_definition
    : '[' NEWL* value (',' NEWL* value NEWL* )* NEWL* ']' # list
    ;

expr
    :   expr '*' expr # Mul
    |   expr '/' expr # Div
    |   expr '+' expr # Add
    |   expr '-' expr # Sub
    |   '(' expr ')' # nested
    |   callMethodChain # callMethodStm
    |   value # expValue
    ;

value
    :   integer_def
    |   double_def
    |   string_def
    |   boolean_def
    |   ret_variable
    |   map_value
    |   mapdef
    |   list_definition
    |   function_def
    ;

function_def
    :   'function(' funcattrib ')' '{' stmt* '}' # functionDefinition
    ;

boolean_def
    :   BOOLEAN # boolean
    ;

double_def
    :   DOUBLE # double
    ;

integer_def
    :   INTEGER # integer
    ;

string_def
    :   STRING # string
    ;

ret_variable
    :   ID  # variable
    ;

funcattrib
    :   (ID)?
    |   ID (',' ID)*
    ;

mapdef
    : '{' NEWL* attrdef? (NEWL+ attrdef)* NEWL* '}' # map
    ;

attrdef
    :   attr_name '=' attr_value
    ;

attr_name
    :   ID
    |   string_def
    ;

attr_value
    :   expr
    ;


map_value
    :   ret_variable '.' ID ( '.' ID )* # mapValue
    ;

RETURN: 'return' ;
PRINT:  'print' ;
BOOLEAN: ( 'true' | 'false' ) ;
DOUBLE: DIGIT+ '.' DIGIT* ;
INTEGER: '-'? DIGIT+ ;

ID: [a-zA-Z_][a-zA-Z0-9_]*  ;
STRING: '"' (ESC | .)*? '"' ;
ESC: '\\"' | '\\\\';

DIGIT: [0-9] ;

NEWL
    : 'r'? '\n'
    | LINE_COMMENT
    ;

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;

WS  :   [ \t]+ -> skip ;