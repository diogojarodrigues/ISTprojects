#ifndef __TIL_AST_DECLARATION_NODE_H__
#define __TIL_AST_DECLARATION_NODE_H__

#include <cdk/ast/expression_node.h>

namespace til {

  /**
   * Class for describing declaration nodes.
   */
  class declaration_node : public cdk::typed_node {
  std::string _identifier;
  int _qualifier;
  cdk::expression_node *_initial;

  public:
    declaration_node(
      int lineno,
      int qualifier,
      std::string &identifier,
      cdk::expression_node *initial,
      std::shared_ptr<cdk::basic_type> type
    ) :
        cdk::typed_node(lineno), _identifier(identifier), _qualifier(qualifier), _initial(initial) {
          this->type(type);
    }

    cdk::expression_node *initial() { return _initial; }

    int qualifier() { return _qualifier; }

    std::string &identifier() { return _identifier; }

    void accept(basic_ast_visitor *sp, int level) { sp->do_declaration_node(this, level); }
  };


} // til

#endif
