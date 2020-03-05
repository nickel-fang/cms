import React,{Component} from 'react';
import { observer, inject } from 'mobx-react';
import { color } from '../utils'
import Page from '../components/Page/Page'
import styles from '../styles/dashboard/index.less'
//import { NumberCard, Quote, Sales, Weather, RecentSales, Comments, Completed, Browser, Cpu, User } from './components'

// @inject('dashboard')
@observer
class PageDashboard extends Component{

    numberCards = () => {
        const {} = this.props
    }

    render(){
        return (
            <div className="p-common">
                <Page className="dashboard">
                    dashboard
                </Page>
            </div>
        )
    }
}

export default PageDashboard;
