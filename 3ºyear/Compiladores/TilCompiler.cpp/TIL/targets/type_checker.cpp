#include <string>
#include "targets/type_checker.h"
#include ".auto/all_nodes.h"  // automatically generated
#include <cdk/types/primitive_type.h>

#include "til_parser.tab.h"

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }

// compare pointers, functions and their content
bool til::type_checker::deepTypeComparison(std::shared_ptr<cdk::basic_type> left,
      std::shared_ptr<cdk::basic_type> right, bool lax) {
  if (left->name() == cdk::TYPE_UNSPEC || right->name() == cdk::TYPE_UNSPEC) {
    return false;
  } else if (left->name() == cdk::TYPE_FUNCTIONAL) {
    if (right->name() != cdk::TYPE_FUNCTIONAL) {
      return false;
    }

    //Compare left and right node types if they are both function types

    auto left_func = cdk::functional_type::cast(left);
    auto right_func = cdk::functional_type::cast(right);

    if (left_func->input_length() != right_func->input_length()
          || left_func->output_length() != right_func->output_length()) {
      return false;
    }

    for (size_t i = 0; i < left_func->input_length(); i++) {
      if (!deepTypeComparison(right_func->input(i), left_func->input(i), lax)) {
        return false;
      }
    }

    for (size_t i = 0; i < left_func->output_length(); i++) {
      if (!deepTypeComparison(left_func->output(i), right_func->output(i), lax)) {
        return false;
      }
    }

    // Return true  if the node types are equal and are both functions
    return true;
  } else if (right->name() == cdk::TYPE_FUNCTIONAL) {
    return false;
  } else if (left->name() == cdk::TYPE_POINTER) {
    if (right->name() != cdk::TYPE_POINTER) {
      return false;
    }

    //Compare leftand right node types if they are both pointer types

    return deepTypeComparison(cdk::reference_type::cast(left)->referenced(),
        cdk::reference_type::cast(right)->referenced(), false);

  } else if (right->name() == cdk::TYPE_POINTER) {
      return false;
  } else if (lax && left->name() == cdk::TYPE_DOUBLE) {
    return right->name() == cdk::TYPE_DOUBLE || right->name() == cdk::TYPE_INT;
  } else {

    //
    return left == right;
  }
}

//---------------------------------------------------------------------------

void til::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++) {
    node->node(i)->accept(this, lvl);
  }
}

//---------------------------------------------------------------------------

void til::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}

void til::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}

void til::type_checker::do_next_node(til::next_node * const node, int lvl) { 
  /* EMPTY */ 
}

void til::type_checker::do_stop_node(til::stop_node * const node, int lvl) { 
  /* EMPTY */ 
}

//---------------------------------------------------------------------------

void til::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void til::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
}

void til::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
}

//---------------------------------------------------------------------------

void til::type_checker::processUnaryExpression(cdk::unary_operation_node *const node, int lvl, bool acceptDoubles) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  if (node->argument()->is_typed(cdk::TYPE_UNSPEC)) {
    node->argument()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->argument()->is_typed(cdk::TYPE_INT) &&
  !(acceptDoubles && node->argument()->is_typed(cdk::TYPE_DOUBLE))){
    throw std::string("wrong type in argument of unary expression");
  }
  
  node->type(node->argument()->type());
}

void til::type_checker::do_unary_minus_node(cdk::unary_minus_node *const node, int lvl) {
  processUnaryExpression(node, lvl, true);
}

void til::type_checker::do_unary_plus_node(cdk::unary_plus_node *const node, int lvl) {
  processUnaryExpression(node, lvl, true);
}

void til::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
  processUnaryExpression(node, lvl, false);
}

void til::type_checker::do_stack_alloc_node(til::stack_alloc_node * const node, int lvl) { 
  ASSERT_UNSPEC;

  node->argument()->accept(this, lvl + 2);

  if (node->argument()->is_typed(cdk::TYPE_UNSPEC)) {
    node->argument()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->argument()->is_typed(cdk::TYPE_INT)) {
    throw std::string("wrong type in argument of stack alloc");
  }

  node->type(cdk::reference_type::create(4 , cdk::primitive_type::create(0, cdk::TYPE_UNSPEC)));
}

//---------------------------------------------------------------------------

//   int, int -> int;
//   int, double -> double;
//   int, pointer -> pointer;
//   double, double -> double;
//   pointer, pointer -> int (both pointers must reference the same type).
void til::type_checker::processBinaryArithmeticExpression(cdk::binary_operation_node *const node, int lvl,
      bool acceptDoubles, bool acceptOnePointer, bool acceptBothPointers) {
  ASSERT_UNSPEC;

  node->left()->accept(this, lvl + 2);

  if (node->left()->is_typed(cdk::TYPE_INT) || node->left()->is_typed(cdk::TYPE_UNSPEC)) {  //left value is a int or unsigned
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_INT) || (acceptDoubles && node->right()->is_typed(cdk::TYPE_DOUBLE))) {
      node->type(node->right()->type());
    } else if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (acceptOnePointer && node->right()->is_typed(cdk::TYPE_POINTER)) {
      node->type(node->right()->type());

      if (node->left()->is_typed(cdk::TYPE_UNSPEC)) {
        node->left()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      }
    } else {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }

    if (node->left()->is_typed(cdk::TYPE_UNSPEC)) {
      node->left()->type(node->type());
    }
  } else if (acceptDoubles && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_INT) || node->right()->is_typed(cdk::TYPE_DOUBLE)) {
      node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } else if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
      node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } else {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else if (acceptOnePointer && node->left()->is_typed(cdk::TYPE_POINTER)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_INT)) {
      node->type(node->left()->type());
    } else if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      node->type(node->left()->type());
    } else if (acceptBothPointers && deepTypeComparison(node->left()->type(), node->right()->type(), false)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else {
    throw std::string("arithmetic binary expression -> left argument ( wrong type )");
  }
}


void til::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
  processBinaryArithmeticExpression(node, lvl, true, true, false);
}
void til::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
  processBinaryArithmeticExpression(node, lvl, true, true, true);
}
void til::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
  processBinaryArithmeticExpression(node, lvl, true, false, false);
}
void til::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
  processBinaryArithmeticExpression(node, lvl, true, false, false);
}
void til::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
  processBinaryArithmeticExpression(node, lvl, false, false, false);
}

void til::type_checker::processBinaryLogicExpression(cdk::binary_operation_node *const node, int lvl,  bool acceptDoubles, bool acceptPointers) {
  ASSERT_UNSPEC;

  node->left()->accept(this, lvl + 2);

  if (node->left()->is_typed(cdk::TYPE_INT)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(node->left()->type());
    } else if (!node->right()->is_typed(cdk::TYPE_INT)
          && !(acceptDoubles && node->right()->is_typed(cdk::TYPE_DOUBLE))
          && !(acceptPointers && node->right()->is_typed(cdk::TYPE_POINTER))) {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else if (acceptDoubles && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(node->left()->type());
    } else if (!node->right()->is_typed(cdk::TYPE_INT) && !node->right()->is_typed(cdk::TYPE_DOUBLE)) {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else if (acceptPointers && node->left()->is_typed(cdk::TYPE_POINTER)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (!node->right()->is_typed(cdk::TYPE_INT) && !node->right()->is_typed(cdk::TYPE_POINTER)) {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else if (node->left()->is_typed(cdk::TYPE_UNSPEC)) {
    node->right()->accept(this, lvl + 2);

    if (node->right()->is_typed(cdk::TYPE_UNSPEC)) {
      node->left()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (node->right()->is_typed(cdk::TYPE_POINTER)) {
      node->left()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (node->right()->is_typed(cdk::TYPE_INT) || (acceptDoubles && node->right()->is_typed(cdk::TYPE_DOUBLE))) {
      node->left()->type(node->right()->type());
    } else {
      throw std::string("arithmetic binary expression -> right argument ( wrong type )");
    }
  } else {
    throw std::string("arithmetic binary expression -> left argument ( wrong type )");
  }

  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void til::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, false);
}
void til::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, false);
}
void til::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, false);
}
void til::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, false);
}
void til::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, true);
}
void til::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, true, true);
}
void til::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, false, false);
}
void til::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
  processBinaryLogicExpression(node, lvl, false, false);
}

//---------------------------------------------------------------------------

void til::type_checker::do_address_of_node(til::address_of_node * const node, int lvl) {
  ASSERT_UNSPEC;

  node->lvalue()->accept(this, lvl + 2);
  if (node->lvalue()->is_typed(cdk::TYPE_POINTER)) {
    auto ref = cdk::reference_type::cast(node->lvalue()->type());

    if (ref->referenced()->name() == cdk::TYPE_VOID) {
      node->type(node->lvalue()->type());
      return;
    }
  }

  node->type(cdk::reference_type::create(4, node->lvalue()->type()));
}

void til::type_checker::do_variable_node(cdk::variable_node *const node, int lvl) {
  ASSERT_UNSPEC;

  // std::shared_ptr<til::symbol> symbol = _symtab.find(node->name()); //TODO: maybe works like this
  auto symbol = _symtab.find(node->name());

   if (symbol == nullptr) {
    throw std::string("undeclared variable '" + node->name() + "'");
  }

  node->type(symbol->type());
}

void til::type_checker::do_index_node(til::index_node * const node, int lvl) {
  ASSERT_UNSPEC;

  node->base()->accept(this, lvl + 2);
  if (!node->base()->is_typed(cdk::TYPE_POINTER)) {
    throw std::string("wrong type in base argument of index node");
  }

  node->index()->accept(this, lvl + 2);
  if (node->index()->is_typed(cdk::TYPE_UNSPEC)) {
    node->index()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->index()->is_typed(cdk::TYPE_INT)) {
    throw std::string("wrong type in pointer index's index (expected integer)");
  }

  auto basetype = cdk::reference_type::cast(node->base()->type());

  if (basetype->referenced()->name() == cdk::TYPE_UNSPEC) {
    basetype = cdk::reference_type::create(4, cdk::primitive_type::create(4, cdk::TYPE_INT));
    node->base()->type(basetype);
  }

  node->type(basetype->referenced());
}


void til::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_UNSPEC;
  try {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  }
}

void til::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_UNSPEC;

  node->lvalue()->accept(this, lvl + 2);
  node->rvalue()->accept(this, lvl + 2);

  if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
    node->rvalue()->type(node->lvalue()->type());
  } else if (node->rvalue()->is_typed(cdk::TYPE_POINTER) && node->lvalue()->is_typed(cdk::TYPE_POINTER)) {
    auto lr = cdk::reference_type::cast(node->lvalue()->type());
    auto rr = cdk::reference_type::cast(node->rvalue()->type());

    if (rr->referenced()->name() == cdk::TYPE_UNSPEC || rr->referenced()->name() == cdk::TYPE_VOID
    || lr->referenced()->name() == cdk::TYPE_VOID) {
      node->rvalue()->type(node->lvalue()->type());
    }
  }

  if (!deepTypeComparison(node->lvalue()->type(), node->rvalue()->type(), true)) {
    throw std::string("wrong type in right argument of assignment expression");
  }

  node->type(node->lvalue()->type());
}

//---------------------------------------------------------------------------

void til::type_checker::do_program_node(til::program_node *const node, int lvl) {
    // auto program = til::make_symbol("@ret", cdk::primitive_type::create(4, cdk::TYPE_VOID));

    // if (!_symtab.insert(program->name(), program)) {
    //   throw std::string("program node '" + program->name() + "' redeclared");
    
    // }
}

void til::type_checker::do_return_node(til::return_node * const node, int lvl) { 
  auto symbol = _symtab.find("@", 1);

  if (symbol == nullptr) {
    throw std::string("return statement outside function");
  }

  std::shared_ptr<cdk::functional_type> function_type = cdk::functional_type::cast(symbol->type());


  auto ret_type = function_type->output(0);
  auto ret_type_name = ret_type->name();

  // no return
  if (node->value() == nullptr) {
    if (ret_type_name != cdk::TYPE_VOID) {
      throw std::string("no return value specified for non-void function");
    }
    return;
  }

  // return has expression 

  if (ret_type_name == cdk::TYPE_VOID) {
    throw std::string("return value specified for void function");
  }

  node->value()->accept(this, lvl + 2);

  if (!deepTypeComparison(ret_type, node->value()->type(), true)) {
    throw std::string("wrong type for return expression");
  }
}

//---------------------------------------------------------------------------

void til::type_checker::do_evaluation_node(til::evaluation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  if (node->argument()->is_typed(cdk::TYPE_UNSPEC)) {
    node->argument()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (node->argument()->is_typed(cdk::TYPE_POINTER)) {
    auto ref = cdk::reference_type::cast(node->argument()->type());

    if (ref != nullptr && ref->referenced()->name() == cdk::TYPE_UNSPEC) {
      node->argument()->type(cdk::reference_type::create(4, cdk::primitive_type::create(4, cdk::TYPE_INT)));
    }
  }
}

void til::type_checker::do_print_node(til::print_node *const node, int lvl) {
  for (size_t i = 0; i < node->argument()->size(); i++) {
    auto child = dynamic_cast<cdk::expression_node*>(node->argument()->node(i));

    child->accept(this, lvl);

    if (child->is_typed(cdk::TYPE_UNSPEC)) {
      child->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (!child->is_typed(cdk::TYPE_INT) && !child->is_typed(cdk::TYPE_DOUBLE)
          && !child->is_typed(cdk::TYPE_STRING)) {
      throw std::string("wrong type for argument " + std::to_string(i + 1) + " of print instruction");
    }
  }
}

//---------------------------------------------------------------------------

void til::type_checker::do_read_node(til::read_node *const node, int lvl) {
  ASSERT_UNSPEC;

  node->type(cdk::primitive_type::create(0, cdk::TYPE_UNSPEC));
}

//---------------------------------------------------------------------------

void til::type_checker::do_loop_node(til::loop_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);

  if (node->condition()->is_typed(cdk::TYPE_UNSPEC)) {
    node->condition()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->condition()->is_typed(cdk::TYPE_INT)) {
    throw std::string("loop instruction-> condition ( wrong type )");
  }
}

//---------------------------------------------------------------------------

void til::type_checker::do_if_node(til::if_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);

  if (node->condition()->is_typed(cdk::TYPE_UNSPEC)) {
    node->condition()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->condition()->is_typed(cdk::TYPE_INT)) {
    throw std::string("conditonal instruction-> condition ( wrong type )");
  }
}

void til::type_checker::do_if_else_node(til::if_else_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);

  if (node->condition()->is_typed(cdk::TYPE_UNSPEC)) {
    node->condition()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (!node->condition()->is_typed(cdk::TYPE_INT)) {
    throw std::string("conditonal instruction-> condition ( wrong type )");
  }
}

//---------------------------------------------------------------------------
void til::type_checker::do_declaration_node(til::declaration_node *const node, int lvl) {
  if (node->type() == nullptr) { // auto
    node->initial()->accept(this, lvl + 2);

    if (node->initial()->is_typed(cdk::TYPE_UNSPEC)) {
      node->initial()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (node->initial()->is_typed(cdk::TYPE_POINTER)) {
      auto ref = cdk::reference_type::cast(node->initial()->type());
      if (ref->referenced()->name() == cdk::TYPE_UNSPEC) {
        node->initial()->type(cdk::reference_type::create(4,
            cdk::primitive_type::create(4, cdk::TYPE_INT)));
      }
    } else if (node->initial()->is_typed(cdk::TYPE_VOID)) {
      throw std::string("cannot declare variable of type void");
    }

    node->type(node->initial()->type());
  } else { // not auto; node already has a type set
    if (node->initial() != nullptr) {
      node->initial()->accept(this, lvl + 2);

      if (node->initial()->is_typed(cdk::TYPE_UNSPEC)) {
        if (node->is_typed(cdk::TYPE_DOUBLE)) {
          node->initial()->type(node->type());
        } else {
          // if node->type() is not an int, a type mismatch error will be thrown later
          node->initial()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        }
      } else if (node->initial()->is_typed(cdk::TYPE_POINTER) && node->is_typed(cdk::TYPE_POINTER)) {
        auto noderef = cdk::reference_type::cast(node->type());
        auto initref = cdk::reference_type::cast(node->initial()->type());
        if (initref->referenced()->name() == cdk::TYPE_UNSPEC
              || initref->referenced()->name() == cdk::TYPE_VOID
              || noderef->referenced()->name() == cdk::TYPE_VOID) {
          node->initial()->type(node->type());
        }
      }

      if (!deepTypeComparison(node->type(), node->initial()->type(), true)) {
        throw std::string("wrong type in initial for variable '" + node->identifier() + "'");
      }
    }
  }

  if (node->qualifier() == tEXTERNAL && !node->is_typed(cdk::TYPE_FUNCTIONAL)) {
    throw std::string("external declaration of non-function '" + node->identifier() + "'");
  }

  auto symbol = make_symbol(node->identifier(), node->type(), node->qualifier());

  if (_symtab.insert(node->identifier(), symbol)) {
    _parent->set_new_symbol(symbol);
    return;
  }

  auto prev = _symtab.find(node->identifier());

  if (prev != nullptr && prev->qualifier() == tFORWARD) {
    if (deepTypeComparison(prev->type(), symbol->type(), false)) {
      _symtab.replace(node->identifier(), symbol);
      _parent->set_new_symbol(symbol);
      return;
    }
  }

  throw std::string("redeclaration of variable '" + node->identifier() + "'");
}

void til::type_checker::do_function_node(til::function_node * const node, int lvl) {
  auto function = til::make_symbol("@", node->type());

  if (!_symtab.insert(function->name(), function)) { // already defined
    _symtab.replace(function->name(), function);
  }
}

void til::type_checker::do_sizeof_node(til::sizeof_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->expression()->accept(this, lvl + 2);

  if (node->expression()->is_typed(cdk::TYPE_UNSPEC)) {
    node->expression()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  }

  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void til::type_checker::do_null_node(til::null_node * const node, int lvl) { 
  ASSERT_UNSPEC;

  node->type(cdk::reference_type::create(4, cdk::primitive_type::create(0, cdk::TYPE_UNSPEC)));
}

void til::type_checker::do_block_node(til::block_node * const node, int lvl) { 
  /* EMPTY */
}

void til::type_checker::do_function_call_node(til:: function_call_node* const node, int lvl) {
  ASSERT_UNSPEC;

  std::shared_ptr<cdk::functional_type> function_type;

  if (node->func() == nullptr) { // recursive call; "@"
    auto symbol = _symtab.find("@", 1);
    if (symbol == nullptr) {
      throw std::string("recursive call outside function");
    }

    function_type = cdk::functional_type::cast(symbol->type());
  } else {
    node->func()->accept(this, lvl);

    if (!node->func()->is_typed(cdk::TYPE_FUNCTIONAL)) {
      throw std::string("wrong type in function call");
    }

    function_type = cdk::functional_type::cast(node->func()->type());
  }

  if (function_type->input()->length() != node->arguments()->size()) {
    throw std::string("wrong number of arguments in function call");
  }

  //comparing args
  for (size_t i = 0; i < node->arguments()->size(); i++) {
    auto arg = dynamic_cast<cdk::expression_node*>(node->arguments()->node(i));
    arg->accept(this, lvl);

    auto paramtype = function_type->input(i);

    if (arg->is_typed(cdk::TYPE_UNSPEC)) {
      if (paramtype->name() == cdk::TYPE_DOUBLE) {
        arg->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
      } else {
        arg->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      }

    } else if (arg->is_typed(cdk::TYPE_POINTER) && paramtype->name() == cdk::TYPE_POINTER) {
      auto paramref = cdk::reference_type::cast(paramtype);
      auto argref = cdk::reference_type::cast(arg->type());

      if (argref->referenced()->name() == cdk::TYPE_UNSPEC
            || argref->referenced()->name() == cdk::TYPE_VOID
            || paramref->referenced()->name() == cdk::TYPE_VOID) {
        arg->type(paramtype);
      }
    }

    if (!deepTypeComparison(paramtype, arg->type(), true)) {
      throw std::string("wrong type for argument " + std::to_string(i + 1) + " in function call");
    }
  }

  node->type(function_type->output(0));
}


