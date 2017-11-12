grammar Fpl;

file
    : block EOF
    ;

block
    : (statement)*
    ;

block_with_braces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | while
    | if
    | assignment
    | return
    ;

function
    : 'fun' Identifier '(' parameterNames ')' block_with_braces
    ;

arguments
    : (expression (',' expression)*)?
    ;

functionCall
    : Identifier '(' arguments ')'
    | PrintLn '(' arguments ')'
    ;

variable
    : 'var' Identifier ('=' expression)?
    ;

parameterNames
    : (Identifier (',' Identifier)*)?
    ;

while
    : 'while' '(' expression ')' block_with_braces
    ;

if
    : 'if' '(' expression ')' block_with_braces ('else' (block_with_braces | if))?
    ;

assignment
    : Identifier '=' expression
    ;

return
    : 'return' expression
    ;

expression
    : functionCall
    | Identifier
    | Literal
    | '(' expression ')'
    | '-' expression
    | expression op = ('*' | '/') expression
    | expression op = ('+' | '-' | '%') expression
    | expression op = ('>' | '<' | '>=' | '<=' | '==' | '!=') expression
    | expression op = '&&' expression
    | expression op = '||' expression
    ;

PrintLn
    : 'println'
    ;

Comment
    : '//' (~[\n\r])* -> skip
    ;

Identifier
    : [a-zA-Z] [a-z0-9_A-Z]*
    ;

Literal
    : ('1'..'9') ('0'..'9')*
    | '0'
    ;

WS : (' ' | '\t' | '\r'| '\n') -> skip;