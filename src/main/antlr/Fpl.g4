grammar Fpl;

file
    : block EOF
    ;

block
    : (statement)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileStatement
    | ifStatement
    | assignment
    | returnStatement
    ;

function
    : 'fun' Identifier '(' parameterNames ')' blockWithBraces
    ;

arguments
    : (expression (',' expression)*)?
    ;

functionCall
    : Identifier '(' arguments ')'
    ;

variable
    : 'var' Identifier ('=' expression)?
    ;

parameterNames
    : (Identifier (',' Identifier)*)?
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : Identifier '=' expression
    ;

returnStatement
    : 'return' expression
    ;

expression
    : functionCall                                                       #functionCallExpression
    | Identifier                                                         #identifierExpression
    | Literal                                                            #literalExpression
    | '(' expression ')'                                                 #bracesExpression
    | '-' expression                                                     #unaryMinusExpression
    | expression op = ('*' | '/') expression                             #binaryExpression
    | expression op = ('+' | '-' | '%') expression                       #binaryExpression
    | expression op = ('>' | '<' | '>=' | '<=' | '==' | '!=') expression #binaryExpression
    | expression op = '&&' expression                                    #binaryExpression
    | expression op = '||' expression                                    #binaryExpression
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