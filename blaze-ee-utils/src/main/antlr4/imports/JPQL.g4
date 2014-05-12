/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
grammar JPQL;

 ql_statement : select_statement | update_statement | delete_statement;

 select_statement : select_clause from_clause (where_clause)? (groupby_clause)? (having_clause)? (orderby_clause)?;

 update_statement : update_clause (where_clause)?;

 delete_statement : delete_clause (where_clause)?;

 from_clause : 'FROM' identification_variable_declaration (',' (identification_variable_declaration | collection_member_declaration))*;

 identification_variable_declaration : range_variable_declaration ( join | fetch_join )*;

 range_variable_declaration : Entity_name ('AS')? Identification_variable;

 join : join_spec join_association_path_expression ('AS')? Identification_variable (join_condition)?;

 fetch_join : join_spec 'FETCH' join_association_path_expression (join_condition)?;

 join_spec : ( 'LEFT' ('OUTER')? | 'INNER' )? 'JOIN';

 join_condition : 'ON' conditional_expression;

 join_association_path_expression : join_collection_valued_path_expression |
                                      join_single_valued_path_expression |
                                      'TREAT('join_collection_valued_path_expression 'AS' Subtype')' |
                                      'TREAT('join_single_valued_path_expression 'AS' Subtype')';

 join_collection_valued_path_expression : Identification_variable'.'(Single_valued_embeddable_object_field'.')*Collection_valued_field;

 join_single_valued_path_expression : Identification_variable'.'(Single_valued_embeddable_object_field'.')*Single_valued_object_field;

 collection_member_declaration : 'IN' '('collection_valued_path_expression')' ('AS')? Identification_variable;

 qualified_identification_variable : composable_qualified_identification_variable |
                                       'ENTRY('Identification_variable')';

 composable_qualified_identification_variable : 'KEY('Identification_variable')' |
                                                  'VALUE('Identification_variable')';

 single_valued_path_expression : qualified_identification_variable |
                                   'TREAT('qualified_identification_variable 'AS' Subtype')' |
                                   state_field_path_expression |
                                   single_valued_object_path_expression;

 general_identification_variable : Identifier |
                                     composable_qualified_identification_variable;

 general_subpath : simple_subpath | treated_subpath('.'Single_valued_object_field)*;
 
 simple_subpath : //general_identification_variable |
                    general_identification_variable('.'Identifier)*;

 treated_subpath : 'TREAT('general_subpath 'AS' Subtype')';

 state_field_path_expression : general_subpath'.'Identifier;

 single_valued_object_path_expression : general_subpath'.'Single_valued_object_field;

 collection_valued_path_expression : general_subpath'.'Collection_valued_field;

 update_clause : 'UPDATE' Entity_name (('AS')? Identification_variable)? 'SET' update_item (',' update_item)*;

 update_item : (Identification_variable'.')?(Single_valued_embeddable_object_field'.')*(State_field | Single_valued_object_field) '=' new_value;

 new_value : scalar_expression |
               simple_entity_expression |
               'NULL';

 delete_clause : 'DELETE' 'FROM' Entity_name (('AS')? Identification_variable)?;

 select_clause : 'SELECT' ('DISTINCT')? select_item (',' select_item)*;

 select_item : select_expression (('AS')? Result_variable)?;

 select_expression : single_valued_path_expression |
                       scalar_expression |
                       aggregate_expression |
                       Identifier |
                       'OBJECT('Identifier')' |
                       constructor_expression;

 constructor_expression : 'NEW' Constructor_name '(' constructor_item (',' constructor_item)* ')';

 constructor_item : single_valued_path_expression |
                      scalar_expression |
                      aggregate_expression |
                      Identification_variable;

 aggregate_expression : ( 'AVG' | 'MAX' | 'MIN' | 'SUM' ) '('('DISTINCT')? state_field_path_expression')' |
                          'COUNT' (('DISTINCT')? Identification_variable |
                                            state_field_path_expression |
                                            single_valued_object_path_expression) |
                          function_invocation;

 where_clause : 'WHERE' conditional_expression;

 groupby_clause : 'GROUP' 'BY' groupby_item (',' groupby_item)*;

 groupby_item : single_valued_path_expression | Identification_variable;

 having_clause : 'HAVING' conditional_expression;

 orderby_clause : 'ORDER' 'BY' orderby_item (',' orderby_item)*;

 orderby_item : state_field_path_expression | Result_variable ( 'ASC' | 'DESC' )?;

 subquery : simple_select_clause subquery_from_clause (where_clause)? (groupby_clause)? (having_clause)?;

 subquery_from_clause : 'FROM' subselect_identification_variable_declaration
                               (',' subselect_identification_variable_declaration | collection_member_declaration)*;

 subselect_identification_variable_declaration : identification_variable_declaration |
                                                   derived_path_expression ('AS')? Identification_variable (join)* |
                                                   derived_collection_member_declaration;

 derived_path_expression : general_derived_path'.'Single_valued_object_field |
                             general_derived_path'.'Collection_valued_field;

 general_derived_path : simple_derived_path |
                          treated_derived_path('.'Single_valued_object_field)*;

 simple_derived_path : Superquery_identification_variable('.'Single_valued_object_field)*;

 treated_derived_path : 'TREAT('general_derived_path 'AS' Subtype')';

 derived_collection_member_declaration : 'IN' Superquery_identification_variable'.'(Single_valued_object_field'.')*Collection_valued_field;

 simple_select_clause : 'SELECT' ('DISTINCT')? simple_select_expression;

 simple_select_expression: single_valued_path_expression |
                             scalar_expression |
                             aggregate_expression |
                             Identification_variable;

 scalar_expression : arithmetic_expression |
                       string_expression |
                       enum_expression |
                       datetime_expression |
                       boolean_expression |
                       case_expression |
                       entity_type_expression;

 conditional_expression : conditional_term | conditional_expression 'OR' conditional_term;

 conditional_term : conditional_factor | conditional_term 'AND' conditional_factor;

 conditional_factor : ('NOT')? conditional_primary;

 conditional_primary : simple_cond_expression | '('conditional_expression')';

 simple_cond_expression : comparison_expression |
                            between_expression |
                            like_expression |
                            in_expression |
                            null_comparison_expression |
                            empty_collection_comparison_expression |
                            collection_member_expression |
                            exists_expression;

 between_expression : arithmetic_expression ('NOT')? 'BETWEEN' arithmetic_expression 'AND' arithmetic_expression |
                        string_expression ('NOT')? 'BETWEEN' string_expression 'AND' string_expression |
                        datetime_expression ('NOT')? 'BETWEEN' datetime_expression 'AND' datetime_expression;

 in_expression : (state_field_path_expression | type_discriminator) ('NOT')? 'IN' ( '(' in_item (',' in_item)* ')' | '('subquery')' | Collection_valued_input_parameter );

 in_item : literal | Single_valued_input_parameter;

 like_expression : string_expression ('NOT')? 'LIKE' Pattern_value ('ESCAPE' escape_character)?;

 escape_character : Character_literal | Character_valued_input_parameter;

 null_comparison_expression : (single_valued_path_expression | Input_parameter) 'IS' ('NOT')? 'NULL';

 empty_collection_comparison_expression : collection_valued_path_expression 'IS' ('NOT')? 'EMPTY';

 collection_member_expression : entity_or_value_expression ('NOT')? 'MEMBER' ('OF')? collection_valued_path_expression;

 entity_or_value_expression : single_valued_object_path_expression |
                                state_field_path_expression |
                                simple_entity_or_value_expression;

 simple_entity_or_value_expression : Identification_variable |
                                       Input_parameter |
                                       literal;

 exists_expression : ('NOT')? 'EXISTS' '('subquery')';

 all_or_any_expression : ( 'ALL' | 'ANY' | 'SOME') '('subquery')';

 comparison_expression : string_expression comparison_operator (string_expression | all_or_any_expression) |
                           boolean_expression ( '=' | '<>' ) (boolean_expression | all_or_any_expression) |
                           enum_expression ( '=' | '<>' ) (enum_expression | all_or_any_expression) |
                           datetime_expression comparison_operator (datetime_expression | all_or_any_expression) |
                           entity_expression ( '=' | '<>' ) (entity_expression | all_or_any_expression) |
                           arithmetic_expression comparison_operator (arithmetic_expression | all_or_any_expression) |
                           entity_type_expression ( '=' | '<>' ) entity_type_expression;

 comparison_operator : '=' | '>' | '>=' | '<' | '<=' | '<>';

 arithmetic_expression : arithmetic_term | arithmetic_expression ( '+' | '-' ) arithmetic_term;

 arithmetic_term : arithmetic_factor | arithmetic_term ( '*' | '/' ) arithmetic_factor;

 arithmetic_factor : ( '+' | '-' )? arithmetic_primary;

 arithmetic_primary : state_field_path_expression |
                        Numeric_literal |
                        '('arithmetic_expression')' |
                        Input_parameter |
                        functions_returning_numerics |
                        aggregate_expression |
                        case_expression |
                        function_invocation |
                        '('subquery')';

 string_expression : state_field_path_expression |
                       String_literal |
                       Input_parameter |
                       functions_returning_strings |
                       aggregate_expression |
                       case_expression |
                       function_invocation |
                       '('subquery')';

 datetime_expression : state_field_path_expression |
                         Input_parameter |
                         functions_returning_datetime |
                         aggregate_expression |
                         case_expression |
                         function_invocation |
                         literal_temporal |
                         '('subquery')';

 boolean_expression : state_field_path_expression |
                        Boolean_literal |
                        Input_parameter |
                        case_expression |
                        function_invocation |
                        '('subquery')';

 enum_expression : state_field_path_expression |
                     Enum_literal |
                     Input_parameter |
                     case_expression |
                     '('subquery')' ;

 entity_expression : single_valued_object_path_expression | simple_entity_expression;

 simple_entity_expression : Identification_variable |
                              Input_parameter;

 entity_type_expression : type_discriminator |
                            Entity_type_literal |
                            Input_parameter;

 type_discriminator : 'TYPE('Identification_variable | single_valued_object_path_expression | Input_parameter ')';

 functions_returning_numerics : 'LENGTH('string_expression')' |
                                  'LOCATE('string_expression',' string_expression (',' arithmetic_expression)? ')' |
                                  'ABS('arithmetic_expression')' |
                                  'SQRT('arithmetic_expression')' |
                                  'MOD('arithmetic_expression',' arithmetic_expression')' |
                                  'SIZE('collection_valued_path_expression')' |
                                  'INDEX('Identification_variable')';

 functions_returning_datetime : 'CURRENT_DATE' | 'CURRENT_TIME' | 'CURRENT_TIMESTAMP';

 functions_returning_strings : 'CONCAT('string_expression',' string_expression (',' string_expression)*')' |
                                 'SUBSTRING('string_expression',' arithmetic_expression (',' arithmetic_expression)?')' |
                                 'TRIM('((trim_specification)? (Trim_character)? 'FROM')? string_expression')' |
                                 'LOWER('string_expression')' |
                                 'UPPER('string_expression')';

 trim_specification : 'LEADING' | 'TRAILING' | 'BOTH';

 function_invocation : 'FUNCTION('String_literal (',' function_arg)*')';

 function_arg : literal |
                  state_field_path_expression |
                  Input_parameter |
                  scalar_expression;

 case_expression : general_case_expression |
                     simple_case_expression |
                     coalesce_expression |
                     nullif_expression;

 general_case_expression : 'CASE' when_clause (when_clause)* 'ELSE' scalar_expression 'END';

 when_clause : 'WHEN' conditional_expression 'THEN' scalar_expression;

 simple_case_expression : 'CASE' case_operand simple_when_clause (simple_when_clause)* 'ELSE' scalar_expression 'END';

 case_operand : state_field_path_expression | type_discriminator;

 simple_when_clause : 'WHEN' scalar_expression 'THEN' scalar_expression;

 coalesce_expression : 'COALESCE('scalar_expression (',' scalar_expression)+')';

 nullif_expression : 'NULLIF('scalar_expression',' scalar_expression')';

 literal
     : Boolean_literal
     | Enum_literal   
     | Numeric_literal
     | String_literal
     ;
     
 

 literal_temporal 
     : Date_literal 
     | Time_literal 
     | Timestamp_literal
     ;

