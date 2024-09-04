#ifndef __TIL_AST_FUNCTION_NODE_H__
#define __TIL_AST_FUNCTION_NODE_H__

#include <cdk/ast/expression_node.h>

namespace til {

  /**
   * Class for describing function_declaration nodes.
   */
  class function_node : public cdk::expression_node {
    til::block_node *_block;
    cdk::sequence_node *_arguments;

  public:
    function_node(
      int lineno, 
      std::shared_ptr<cdk::basic_type> return_type,
      til::block_node *block,
      cdk::sequence_node *arguments
    ) :
        cdk::expression_node(lineno), _block(block), _arguments(arguments) {
      
          std::vector<std::shared_ptr<cdk::basic_type>> argument_types;
          for (size_t i = 0; i < arguments->size(); i++) {
            argument_types.push_back(dynamic_cast<cdk::typed_node*>(arguments->node(i))->type());
          }
      this->type(cdk::functional_type::create(argument_types, return_type));

    }

    til::block_node *block() { return _block; }
    
    cdk::sequence_node *arguments() { return _arguments;}

    void accept(basic_ast_visitor *sp, int level) { sp->do_function_node(this, level); }

  };

} // til

#endif
