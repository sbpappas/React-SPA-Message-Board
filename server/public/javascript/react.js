console.log("running react js stuff");

const ce = React.createElement

class LoginComponent extends React.Component{
    render(){
        
    }
}

ReactDOM.render(
    ce('div', null,
        ce(Hello, {toWhat: 'World'}, null),
        ce(StatelessHello, {toWhat: 'Earth'}, null),
        ce(SimpleForm, null, null),
        ),
    document.getElementById('react-root')
)