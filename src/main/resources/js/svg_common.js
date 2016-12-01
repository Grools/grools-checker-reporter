var drag = {
    elem: null,
    x: 0,
    y: 0,
    state: false
};
var delta = {
    x: 0,
    y: 0
};

Array.prototype.hasObject = (
  !Array.indexOf ? function (o)
  {
    var l = this.length + 1;
    while (l -= 1)
    {
        if (this[l - 1] === o)
        {
            return true;
        }
    }
    return false;
  } : function (o)
  {
    return (this.indexOf(o) !== -1);
  }
);

function hasClass(el, className) {
  if( typeof el == 'undefined' )
    return false;
  else if (el.classList)
    return el.classList.contains(className);
  else
    return !!el.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'));
}

function addClass(el, className) {
  if( typeof el != 'undefined' ){
      if (el.classList)
        el.classList.add(className);
      else if (!hasClass(el, className))
        el.className += ' ' + className;
  }
}

function removeClass(el, className) {
  if( typeof el != 'undefined' ){
      if (el.classList)
        el.classList.remove(className);
      else if (hasClass(el, className)) {
        var reg = new RegExp('(\\s|^)' + className + '(\\s|$)');
        el.className=el.className.replace(reg, ' ');
      }
  }
}
function move( tooltips, x , y ){
    const shift  = computeShift(x, y);
    let coordX   = 0;
    let coordY   = 0;
    if (!tooltips.style.left)
        coordX = (shift.x > 30)? shift.x - 30 : 0;
    else
        coordX = (shift.x - shift.left > 30)? shift.x - shift.left - 30 : 0;
    if (!tooltips.style.top)
        coordY = (shift.y > 30)? shift.y - 30 : 0;
    else
        coordY = (shift.y - shift.top > 30)? shift.y - shift.top - 30 : 0;
    return { x: coordX, y: coordY};
}

function tooltips_event( node, title, description, color, graph, path ){
    const tooltips = createInformativeNode(node, title, description, color );
    document.body.appendChild(tooltips);
    const text      = node.getElementsByTagName( 'text' )[0];
    const button    = tooltips.getElementsByTagName("button")[0];
    const header    = tooltips.getElementsByClassName("header")[0];

    text.addEventListener( 'click',  function( event ) {
        tooltips.style.display = 'block';
    } );

    button.addEventListener( 'click',  function( event ) {
        tooltips.style.display = 'none';
        drag = {
            elem: null,
            x: 0,
            y: 0,
            state: false
        };
    }, false );

    header.addEventListener( 'mousedown', function(e){
        let movement = move(tooltips, e.clientX, e.clientY);
        drag = {
            elem: tooltips,
            x: movement.x,
            y: movement.y,
            state: true
        };
    }, false );

    node.addEventListener( 'click',  function( event ) {
        if( event.target.nodeName != 'text' ){
          graph.forEach( function( item ){
                if( path.hasObject( item ) )
                    item.style.opacity = 1;
                else
                    item.style.opacity = 0.5;
          }  );
        }
    } );
}
function computeShift( clientX, clientY ){
    const doc           = document.documentElement;
    const scrollLeft    = (window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0);
    const scrollTop     = (window.pageYOffset || doc.scrollTop)  - (doc.clientTop  || 0);
    const offsetX       = clientX - scrollLeft;
    const offsetY       = clientY - scrollTop;
    return {
            x:      offsetX,
            y:      offsetY,
            left:   scrollLeft,
            top:    scrollTop
           };
}

function createInformativeNode( node, title, text, color ){
  const tooltips       = document.createElement('div');
  const header         = document.createElement('div');
  const title_span     = document.createElement('span');
  const button         = document.createElement('button');
  const button_img     = document.createElement('span');
  const p              = document.createElement('p');
  title_span.innerHTML = title;
  title_span.className = 'title';
  button.appendChild( button_img );
  header.className      = 'header';
  header.appendChild( title_span );
  header.appendChild( button );
  tooltips.id           = 'tooltips-'+node.id;
  tooltips.className    = 'tooltips';
  tooltips.style.display= 'none';
  tooltips.appendChild( header );
  p.innerHTML           = text;
  tooltips.appendChild( p );
  return tooltips;
}

function getPathChildToParent( node_id, nodes, edges, path ){
    path.push( nodes.filter( n => n.id == node_id )[0] );
    for( var edge_index=0; edge_index < edges.length; edge_index++ ){
        var text        = edges[ edge_index ].getElementsByTagName('title')[0].textContent;
        var relations   = text.split('->'); // 0: source 1: target
        if( node_id == relations[0] ){
            path.push( nodes.filter( n => n.id == relations[1] )[0] );
            path.push( edges[ edge_index ] )
            getPathChildToParent( relations[1], nodes, edges, path );
        }
    }
    return path;
}


function getPathParentToChild( node_id, nodes, edges, path ){
    const parent = nodes.filter( n => n.id == node_id )[0];
    path.push( parent );
    for( var edge_index=0; edge_index < edges.length; edge_index++ ){
        const text        = edges[ edge_index ].getElementsByTagName('title')[0].textContent;
        const relations   = text.split('->'); // 0: child 1: parent
        if( node_id == relations[1] ){
            const child = nodes.filter( n => n.id == relations[0] )[0];
            path.push( child );
            path.push( edges[ edge_index ] )
            getPathParentToChild( relations[0], nodes, edges, path );
        }
    }
    return path;
}

document.onmousedown=function(e){
}

document.onmousemove=function(e){
    if( drag.state ){
        const movement          = move( drag.elem, e.clientX, e.clientY );
        drag.elem.style.left    = movement.x  + 'px';
        drag.elem.style.top     = movement.y  + 'px';
    }
}
document.onmouseup=function(e){
    if( drag.state ){
        drag = {
            elem: null,
            x: 0,
            y: 0,
            state: false
        };
    }
}